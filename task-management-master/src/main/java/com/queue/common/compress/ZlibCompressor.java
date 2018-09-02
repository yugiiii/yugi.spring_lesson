package com.queue.common.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Zip <-> Stringの変換をする
 */
public class ZlibCompressor {

	// 圧縮されたバイト列を扱うチャンクサイズ
    static final int COMPRESSED_CHUNK_SIZE = 512;

    // 圧縮されていないバイト列を扱うチャンクサイズ
    static final int ORIGINAL_CHUNK_SIZE = 512;
    
    // 文字列をZipに圧縮する
    public static byte[] compress(final String str) throws IOException {
    	if(StringUtils.isEmpty(str)) {
    		return null;
    	}
    	byte[] input = str.getBytes("UTF-8");
        byte[] compressedData;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
        	Deflater compressor = new Deflater();
        	try{
        		byte[] outbufs = new byte[COMPRESSED_CHUNK_SIZE];
        		
        		compressor.setInput(input);
        		compressor.finish();
        		while(true) {
        			int size = compressor.deflate(outbufs);
        			if(size > 0) {
        				baos.write(outbufs, 0, size);
        			} else {
        				break;
        			}
        		}
        	} finally {
				compressor.end();
			}
        	compressedData = baos.toByteArray();
        }
        return compressedData;
    }
    
    // zipを文字列に変換をする
    public static String decompress(final byte[] compressed) throws IOException, DataFormatException {
    	String outStr = "";
    	if(ArrayUtils.isEmpty(compressed)) {
    		return "";
    	}
    	
    	final Inflater decompresser = new Inflater();
    	try {
            try (
            	InputStream bais = new ByteArrayInputStream(compressed);
            	ByteArrayOutputStream baos = new ByteArrayOutputStream()
            ) {
                final byte[] inpBuf = new byte[COMPRESSED_CHUNK_SIZE];
                final byte[] outBuf = new byte[ORIGINAL_CHUNK_SIZE];

                int rd;
                do {
                    rd = bais.read(inpBuf);
                    if (rd > 0) {
                        decompresser.setInput(inpBuf, 0, rd);
                    }
                    while (!decompresser.finished()) {
                        int siz = decompresser.inflate(outBuf);
                        if (siz > 0) {
                        	baos.write(outBuf, 0, siz);
//                            // 実質上、ASCII文字のみと想定している.
//                            // (マルチバイト文字列の分割位置を考慮していない)
//                            outStr = new String(outBuf, 0, siz, "UTF-8");
                        } else {
                            break;
                        }
                    }
                } while (rd > 0);
                
                outStr = new String(baos.toByteArray(), "UTF-8");
            }
        } finally {
            decompresser.end();
        }
    	return outStr;
    }
	
}
