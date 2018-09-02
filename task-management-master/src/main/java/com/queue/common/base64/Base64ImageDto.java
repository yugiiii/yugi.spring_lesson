package com.queue.common.base64;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.queue.common.exception.WarningException;


/**
 * base64のimageをdecodeするdto
 * @author nagataryou
 *
 */
@Component
public class Base64ImageDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] imageBytes;
    private String fileName;
    private String fileType;
    private static final List<String> VALID_FILE_TYPES = new ArrayList<String>();
    
    /**
     * サポートする画像ファイル
     */
    static {
        VALID_FILE_TYPES.add("jpg");
        VALID_FILE_TYPES.add("jpeg");
        VALID_FILE_TYPES.add("png");
        VALID_FILE_TYPES.add("svg+xml");
    }

    public Base64ImageDto(String base64ImageData, String fileName) {
        this.fileName = fileName;
        String[] base64Components = StringUtils.split(base64ImageData, ",");

        if (base64Components.length != 2) {
            throw new WarningException("errors_image_failencrypt", "画像データのアップロードに失敗しました。");
        }

        String base64Data = base64Components[0];
        this.fileType = base64Data.substring(base64Data.indexOf('/') + 1, base64Data.indexOf(';'));

        if (!VALID_FILE_TYPES.contains(fileType)) {
            throw new WarningException("errors_image_failencrypt", "サポートできないファイルタイプです。");
        }

       String base64Image = base64Components[1];
       this.imageBytes = DatatypeConverter.parseBase64Binary(base64Image);
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
