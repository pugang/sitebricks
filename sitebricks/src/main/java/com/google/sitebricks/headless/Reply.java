package com.google.sitebricks.headless;

import com.google.inject.Injector;
import com.google.sitebricks.client.Transport;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Reply to a (headless) web request.
 */
public abstract class Reply<E> {
  // Asks sitebricks to continue down the servlet processing chain
  public static final Reply<?> NO_REPLY = Reply.saying();

  public static final String NO_REPLY_ATTR = "sb_no_reply";

  public abstract Reply<E> seeOther(String uri);

  public abstract Reply<E> seeOther(String uri, int statusCode);

  public abstract Reply<E> type(String mediaType);

  public abstract Reply<E> headers(Map<String, String> headers);

  public abstract Reply<E> notFound();

  public abstract Reply<E> unauthorized();

  public abstract Reply<E> as(Class<? extends Transport> transport);

  public abstract Reply<E> redirect(String url);

  public abstract Reply<E> forbidden();

  public abstract Reply<E> noContent();

  public abstract Reply<E> error();

  public abstract Reply<E> status(int code);

  public abstract Reply<E> ok();

  abstract void populate(Injector injector, HttpServletResponse response) throws IOException;

  public static <E> Reply<E> saying() {
    return new ReplyMaker<E>(null);
  }

  public static <E> Reply<E> with(E entity) {
    return new ReplyMaker<E>(entity);
  }
}
