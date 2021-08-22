package io.github.oliviercailloux.diet.utils;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.VerifyException;
import io.github.oliviercailloux.jaris.exceptions.Unchecker;
import java.net.URI;

public class Utils {
	/**
	 * @param host square brackets are added if the host is a literal IPv6 address
	 *             without the brackets
	 * @param path Any character not in the unreserved, punct, escaped, or other
	 *             categories, and not equal to the slash character ('/') or the
	 *             commercial-at character ('@'), is quoted. Must be absolute (start
	 *             with a slash).
	 * @return the absolute (that is, with-scheme), hierarchical (equivalently, not
	 *         opaque, that is, as it is absolute, whose scheme specific part starts
	 *         with a slash) URI "http://", followed by the host, followed by the
	 *         path.
	 * @throws IllegalArgumentException if the path is relative
	 * @throws VerifyException          If the URI string constructed from the given
	 *                                  components violates RFC 2396
	 */
	public static URI http(String host, String path) {
		/*
		 * Check to avoid VerifyException (wrapping URISyntaxException) for relative
		 * paths.
		 */
		checkArgument(path.startsWith("/"));
		return Unchecker.wrappingWith(VerifyException::new).getUsing(() -> new URI("http", host, path, null));
	}

	/**
	 * @param host square brackets are added if the host is a literal IPv6 address
	 *             without the brackets
	 * @param path Any character not in the unreserved, punct, escaped, or other
	 *             categories, and not equal to the slash character ('/') or the
	 *             commercial-at character ('@'), is quoted. Must be absolute (start
	 *             with a slash).
	 * @return the absolute (that is, with-scheme), hierarchical (equivalently, not
	 *         opaque, that is, as it is absolute, whose scheme specific part starts
	 *         with a slash) URI "https://", followed by the host, followed by the
	 *         path.
	 * @throws IllegalArgumentException if the path is relative
	 * @throws VerifyException          If the URI string constructed from the given
	 *                                  components violates RFC 2396
	 */
	public static URI https(String host, String path) {
		/*
		 * Check to avoid VerifyException (wrapping URISyntaxException) for relative
		 * paths.
		 */
		checkArgument(path.startsWith("/"));
		return Unchecker.wrappingWith(VerifyException::new).getUsing(() -> new URI("https", host, path, null));
	}
}
