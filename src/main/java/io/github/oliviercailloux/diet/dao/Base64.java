package io.github.oliviercailloux.diet.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.io.BaseEncoding;
import java.nio.charset.StandardCharsets;

public class Base64 {
	private static final BaseEncoding ENCODER = BaseEncoding.base64();

	public static Base64 from(String unencoded) {
		return new Base64(ENCODER.encode(unencoded.getBytes(StandardCharsets.UTF_8)));
	}

	public static Base64 alreadyBase64(String base64) {
		return new Base64(base64);
	}

	private final String base64;

	private Base64(String base64) {
		this.base64 = checkNotNull(base64);
	}

	public String getUnencoded() {
		return new String(ENCODER.decode(base64), StandardCharsets.UTF_8);
	}

	public String getRawBase64String() {
		return base64;
	}
}
