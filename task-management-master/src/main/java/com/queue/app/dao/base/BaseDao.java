package com.queue.app.dao.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.queue.common.constant.Singleton;

public abstract class BaseDao {
	
	@Autowired
    private NamedParameterJdbcTemplate npJdbcTemplate;
	
	protected Class<?> resultObject;
	
	protected MapSqlParameterSource createParamMap() {
		MapSqlParameterSource param = new MapSqlParameterSource();
		param.addValue("virtualnow", new Date(Singleton.getServerTime()));
		return param;
	}
	
	/**
	 * あるクラスが所属しているディレクトリのパスを取得する
	 * @param className
	 * @return
	 */
	protected String getDeclearFilePath(Class<?> className) { // 引数はclass Class
		// 呼び出しているルートとなるプロジェクトのパスを取得する
		final String projectRootPath = new File(".").getAbsoluteFile().getParent(); // このファイルからみてAbsoluteFileを取得し、その親のディレクトリを取得
		String[] classElements = className.getName().toString().split("\\.");
		// 取得したクラスからディレクトリのパスを生成する
		String filePath = projectRootPath;
		filePath += "/src/main/java/";
		// 最後の一つはファイル名のために取得しない
		for (int i = 0; i < classElements.length - 1; i++) {
			filePath += classElements[i] + "/";
		}
		return filePath;
	}
	
	/**
	 * 指定したSQLファイルの中身を取得する
	 * @param filename
	 * @return
	 */
	protected String readQueryBySqlFile(String filename) {
		// 指定したSQLファイルを呼び出す
		File file = new File(getDeclearFilePath(this.getClass()) + "sql/" + filename + ".sql"); // baseDaoを継承したclassからthis.getClass()をすればそのクラスになる
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			// sqlを読む
			String sqlString = "";
			String line = br.readLine();
			while (line != null) {
				sqlString += line;
				sqlString += "\r";
				line = br.readLine();
			}
			br.close();
			return sqlString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * メソッド名からSQLファイルを取得する
	 * @return
	 */
	protected String readQuery() {
		StackTraceElement[] currentMethod = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTraceElement : currentMethod) {
			if (this.getClass().getName() == stackTraceElement.getClassName()) {
				return readQueryBySqlFile(stackTraceElement.getMethodName());
			}
		}
		return null;
	}
	
	/**
	 * コンストラクタで指定した型にキャストされる
	 * @return
	 */
	protected Object select() {
		return select(resultObject);
	}
	
	protected Object select(Class<?> returnClass) {
		return returnClass.cast(npJdbcTemplate.queryForMap(readQuery(), new MapSqlParameterSource()));
	}
	
	/**
	 *  1レコード取得
	 */
	protected Object select(MapSqlParameterSource param) {
		try{
			RowMapper mapper = new BeanPropertyRowMapper(resultObject); // このマッパーがsnakeToCamelを変換してくれている
			Object list = npJdbcTemplate.queryForObject(readQuery(), param, mapper);
			return list;
		} catch (EmptyResultDataAccessException e) {
			// nullを代入することができる
			return null;
		}
	}
	
	/**
	 * 一番最後のinsertしたid番号を取得する
	 * column name = max(id) as id
	 * でsql文を描いてください
	 * @return
	 */
	protected long selectLastInsert() {
		Map<String, Object> list = npJdbcTemplate.queryForMap(readQuery(), new MapSqlParameterSource());
		return Long.parseLong(list.get("id").toString());
	}
	
	/**
	 * 一番最後のinsertしたid番号を取得する
	 * column name = max(id) as id
	 * でsql文を描いてください
	 * @return
	 */
	protected int selectLastInsertInteger() {
		Map<String, Object> list = npJdbcTemplate.queryForMap(readQuery(), new MapSqlParameterSource());
		return Integer.parseInt(list.get("id").toString());
	}
	
	/**
	 * id番号を取得する
	 * column name = max(id) as id
	 * でsql文を描いてください
	 * @return
	 */
	protected int selectId(MapSqlParameterSource param) {
		Map<String, Object> list = npJdbcTemplate.queryForMap(readQuery(), param);
		return Integer.parseInt(list.get("id").toString());
	}
	
	protected Object selectAll(MapSqlParameterSource param) {
		RowMapper mapper = new BeanPropertyRowMapper(resultObject);
		Object list = npJdbcTemplate.query(readQuery(), param, mapper);
		return list;
	}
	
	/**
	 * データの数をカウントして返す
	 */
	protected Integer getCount(MapSqlParameterSource param) {
		Map<String, Object> list = npJdbcTemplate.queryForMap(readQuery(), param);
		return Integer.parseInt(list.get("count").toString());
	}
	
	/**
	 * insertする
	 */
	protected Integer insert(Object object) {
		MapSqlParameterSource param = new MapSqlParameterSource();
		Field[] fields = object.getClass().getFields(); // objectのpublic classの値を全て取得する
		try {
			for (Field o : fields) {
				param.addValue(o.getName(), o.get(object));
			}
			param.addValue("virtualnow", new Date(Singleton.getServerTime()));
		} catch (IllegalAccessException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return npJdbcTemplate.update(readQuery(), param);
	}
	
	/**
	 * データをinsertする
	 * @param param
	 * @return
	 */
	protected Integer insert(MapSqlParameterSource param) {
		param.addValue("virtualnow", new Date(Singleton.getServerTime()));
		return npJdbcTemplate.update(readQuery(), param);
	}
	
	/**
	 * データの更新を行う
	 * @param param
	 * @return
	 */
	protected Integer update(MapSqlParameterSource param) {
		param.addValue("virtualnow", new Date(Singleton.getServerTime()));
		return npJdbcTemplate.update(readQuery(), param);
	}
	
	/**
	 * Dtoをそのままupdateする時に使用する
	 * @param object
	 * @return
	 */
	protected Integer update(Object object) {
		MapSqlParameterSource param = new MapSqlParameterSource();
		Field[] fields = object.getClass().getFields();
		try {
			for (Field o : fields) {
				param.addValue(o.getName(), o.get(object));
			}
			param.addValue("virtualnow", new Date(Singleton.getServerTime()));
		} catch (IllegalAccessException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return npJdbcTemplate.update(readQuery(), param);
	}
}