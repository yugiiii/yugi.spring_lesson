package com.queue.common.encrypt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.MessageDigest;
import java.util.zip.DataFormatException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;

import com.queue.common.compress.ZlibCompressor;
import com.queue.common.exception.ErrorException;


public class Encryptor {
	private static String AES_MODE = "AES/CBC/PKCS5Padding";
	
	/*
	 * byte列を文字列にエンコード/デコードする仕組みをEnumで定義
	 */
	enum Converter {
		HEX_BINARY {
			@Override
			byte[] decode(String s) {
				return DatatypeConverter.parseHexBinary(s);
			}

			@Override
			String encode(byte[] b) {
				return DatatypeConverter.printHexBinary(b);
			}
		};

		abstract byte[] decode(String s);
		abstract String encode(byte[] b);
	}
	
	/**
	 * 秘密鍵をバイト列から生成する
	 * 
	 * @param key_bits
	 *            鍵の長さ（ビット単位）
	 */
	private static Key makeKey(String key) {
		byte[] keyByte = key.getBytes();
		return new SecretKeySpec(keyByte, "AES");
	}
	
	/**
	 * AESで暗号化する
	 */
	public static String aesEncrypt(String src, String key) throws UnsupportedEncodingException {
		Key skey = makeKey(key);
		byte[] srcByte = src.getBytes("UTF-8");
		return aesEncrypt(srcByte, skey, Converter.HEX_BINARY);
	}

	
	/**
	 * 	AESで暗号化をする
	 */
	public static String aesEncrypt(byte[] src, Key skey, Converter converter) {
		try {
			Cipher cipher = Cipher.getInstance(AES_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			byte[] iv = cipher.getIV();
			byte[] enc = cipher.doFinal(src);
			byte[] ret = new byte[iv.length + enc.length];
			System.arraycopy(iv, 0, ret, 0, iv.length);
			System.arraycopy(enc, 0, ret, iv.length, enc.length);
			return converter.encode(ret);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * AESで復号化する。
	 * 
	 * @param src
	 * @param key
	 * @return
	 * @throws ErrorException 
	 */
	public static String aesDecrypt(String src, String key) throws ErrorException {
		Key skey = makeKey(key);
		try {
			return new String(aesDecode(src, skey, Converter.HEX_BINARY), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * AESで復号化
	 * @throws ErrorException 
	 */
	public static byte[] aesDecode(String src, Key skey, Converter converter) throws ErrorException {
		try {
			byte[] srcByte = converter.decode(src);
			Cipher cipher = Cipher.getInstance(AES_MODE);
			final int BLOCK_SIZE = cipher.getBlockSize();
			
			AlgorithmParameters iv = AlgorithmParameters.getInstance("AES");
			byte[] ib = new byte[2 + BLOCK_SIZE];
			ib[0] = 4; // getEncoded()で取った値がこういう数字になっている
			ib[1] = (byte) BLOCK_SIZE; // 動きはするけど、これで正しいのかどうかは不明
			System.arraycopy(srcByte, 0, ib, 2, BLOCK_SIZE);
			iv.init(ib);

			cipher.init(Cipher.DECRYPT_MODE, skey, iv);
			return cipher.doFinal(srcByte, BLOCK_SIZE, srcByte.length - BLOCK_SIZE);
		} catch (Exception e) {
			throw new ErrorException("errors_session_invalid", 401, "セッションが間違っています。再度ログインしてください");
		}
	}
	
	
	/**
	 * AESで復号化してから、Zlibを解凍
	 * @throws ErrorException 
	 */
	public static String aesDecryptAndZlibDecompress(String src, String key) throws ErrorException{
		Key skey = makeKey(key);
		try {
			return ZlibCompressor.decompress(aesDecode(src, skey, Converter.HEX_BINARY));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (DataFormatException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Zlibで圧縮してから、AESで暗号化をする
	 */
	public static String zlibCompressAndAesEncrypt(String src, String key) throws IOException {
		Key skey = makeKey(key);
		byte[] srcByte = ZlibCompressor.compress(src);
		return aesEncrypt(srcByte, skey, Converter.HEX_BINARY);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String hash256(String key) {
		byte[] cipher_byte;
		try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(key.getBytes());
            cipher_byte = md.digest();
            StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
            for(byte b: cipher_byte) {
            	sb.append(String.format("%02x", b&0xff) );
            }
            return sb.toString();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	/**
	 * AESで暗号化（ECBを使用=同じ平文から作成される暗号文が同じ文になる）
	 * 
	 * @param src
	 * @param key
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String aesEncryptWithEcb(String src, String key) throws UnsupportedEncodingException {
		Key skey = makeKey(key);
		byte[] srcByte = src.getBytes("UTF-8");
		
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			byte[] enc = cipher.doFinal(srcByte);
			return Converter.HEX_BINARY.encode(enc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * AESで復号化（ECBを使用=同じ平文から作成される暗号文が同じ文になる）
	 */
	public static String aesDecryptWithEcb(String src, String key) {
		Key skey = makeKey(key);
		try {
			byte[] srcByte = Converter.HEX_BINARY.decode(src);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			
			return new String(cipher.doFinal(srcByte), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * MD5
	 * 
	 * @param text
	 * @return
	 */
	public static String md5Encrypt(String text) {
		return DigestUtils.md5Hex(text);
	}
}
