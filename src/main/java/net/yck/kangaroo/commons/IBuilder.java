package net.yck.kangaroo.commons;

public interface IBuilder<T> {

  T build() throws BuilderException;

}
