package com.japzio.monitor.validator;

import java.util.List;

public interface Validator<T> {

    public void validate(T t, List<String> errors);

}
