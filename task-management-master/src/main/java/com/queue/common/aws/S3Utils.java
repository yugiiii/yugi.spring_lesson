package com.queue.common.aws;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.queue.common.exception.ErrorException;
import com.queue.common.exception.WarningException;

/**
 * S3へのアップロード及びダウンロード関係のファイル
 * @author shibatanaoto
 *
 */
@Service
public class S3Utils {
	
	public final Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private AmazonS3Client amazonS3Client;
	
	// application.ymlのプロパティ一覧
	@Value("${cloud.aws.region}")
	private String region;
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	
	public String getUrlFromRegionAndBucket() {
		return "https://s3-" + region + ".amazonaws.com/" + bucket;
	}
	
	/**
	 * ファイルパスからデータをアップロードする
	 * @param filePath
	 * @param uploadKey
	 * @return
	 * @throws FileNotFoundException
	 */
	private PutObjectResult upload(String filePath, String uploadKey) throws FileNotFoundException {
		// ファイル名からinput streamを取得する
		return upload(new FileInputStream(filePath), uploadKey);
	}

	/**
	 * input streamからのデータをアップロードする
	 * @param inputStream
	 * @param uploadKey
	 * @return
	 */
	public PutObjectResult upload(InputStream inputStream, String uploadKey) {
		// Objectをputするためのインスタンスを作成
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, uploadKey, inputStream, new ObjectMetadata());
		
		// 読み取り権限を公開にする
		putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
		
		// オブジェクトをアップロードしてその結果を取得する
		PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);
		
		// input streamを閉じる
		IOUtils.closeQuietly(inputStream);
		return putObjectResult;
	}
	
	/**
	 * マルチパートのデータをS3にアップロードする
	 * @param multipartFile
	 * @return
	 */
	public PutObjectResult upload(MultipartFile multipartFile) throws ErrorException {
		// 結果の格納先を準備
		PutObjectResult putObjectResult = new PutObjectResult();
		try {
			// マルチパートファイルをアップロードをする
			putObjectResult = upload(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
		} catch (IOException e) {
			// ファイルがマルチパートでない場合はBAD REQUESTを投げる
			throw new ErrorException("File is not multipart.", 400, "ファイルの送信形式が違います。");
		}
		return putObjectResult;
	}

	/**
	 * 複数マルチパートファイルのデータをS3にアップロードする
	 * 基本的にはこちらを使用する
	 * @param multipartFiles
	 * @return
	 */
	public List<PutObjectResult> upload(MultipartFile[] multipartFiles) {
		List<PutObjectResult> putObjectResults = new ArrayList<>();
		
		// マルチパートの配列を全てアップロードする
		Arrays.stream(multipartFiles)
				.filter(multipartFile -> !StringUtils.isEmpty(multipartFile.getOriginalFilename()))
				.forEach(multipartFile -> {
					try {
						putObjectResults.add(upload(multipartFile.getInputStream(), multipartFile.getOriginalFilename()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

		return putObjectResults;
	}
	
	/**
	 * バケット内のキーからファイルをダウンロードして来る
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public ResponseEntity<byte[]> download(String key) throws WarningException {
		// バケットとキー名からオブジェクトを取得する
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
		S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
		
		// S3のオブジェクトのストリームを取得する(InputStream + abort())
		S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
		
		// データの準備をする
		byte[] bytes = null;
		String fileName = "";
		
		try {
			// S3オブジェクトの中身をバイト列として取得する
			bytes = IOUtils.toByteArray(objectInputStream);
		
			// keyをHTTPリクエスト用にURL Encodeする
			fileName = URLEncoder.encode(key, "UTF-8").replaceAll("\\+", "%20");
		} catch (IOException e) {
			throw new WarningException("errors.key.not-found", "サーバーエラーです");
		}
		// オブジェクトダウンロード時のHTTPヘッダーの指定
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.setContentLength(bytes.length);
		httpHeaders.setContentDispositionFormData("attachment", fileName);

		return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
	}
	
	/**
	 * バケットの中身をまとめて取得する
	 * @return
	 */
	public List<S3ObjectSummary> list() {
		// 指定したバケット内のオブジェクトのリストを取得する
		ObjectListing objectListing = amazonS3Client.listObjects(new ListObjectsRequest().withBucketName(bucket));
		// バケット内の中身のサマリーを取得する
		List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
		
		return s3ObjectSummaries;
	}
	
	/**
	 * bucketとkey名からオブジェクトの公開リンクを取得します。
	 * @param key
	 * @return
	 */
	public String getObjectLink(String key) {
		return "https://s3-" + region + ".amazonaws.com/" + bucket + "/" + key;
	}
	
}
