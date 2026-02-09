package jp.example;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

import java.time.LocalTime;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

/**
 * リクエストパラメーターを受け取り、JSON を返す REST API サーブレットです。
 * <pre>
 * 初回実行方法: プロジェクト右クリック → デバッグ → サーバーでデバッグ
 * (Eclipse は実行ではなくデバッグ実行することで、再起動なしでメソッド内 Java コード変更が反映される)
 * → Tomcat10_Java21 選択
 * → 完了 (2 回目以降はサーバービューからデバッグ実行)
 * </pre>
 */
@WebServlet("/")
@Log
public class HelloServlet extends HttpServlet {

	@Data
	public static class HelloParam {
		
		@NotNull
		@Pattern(regexp = "^[^ -~｡-ﾟ]*$", message = "全角以外の文字が含まれています '${validatedValue}'")
		public String name;
		
		@Size(min = 2, max = 3)
		public String[] values;
	}

	@Data
	public static class HelloResult {
		public String message;
		public LocalTime time;
		public HelloParam param;
		public Map<String, String> errors;
	}

	@Override
	@SneakyThrows
	protected void doGet(HttpServletRequest req, HttpServletResponse res) {
		
		HelloParam param = new HelloParam();
		BeanUtils.populate(param, req.getParameterMap());
		
		HelloResult result = new HelloResult();
		result.message = "こんにちは、%sさん！".formatted(ofNullable(param.name).orElse("名無し"));
		result.time = LocalTime.now();
		result.param = param;
		result.errors = Validation.buildDefaultValidatorFactory().getValidator().validate(param)
				.stream().collect(toMap(v -> v.getPropertyPath().toString(), v -> v.getMessage()));
		
		log.info("処理結果: " + result);
		res.setContentType("application/json");
		JsonbBuilder.create().toJson(result, res.getWriter());
	}
}
