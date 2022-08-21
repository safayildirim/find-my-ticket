package com.safayildirim.findmyticket;

public interface OnCompleteListener<T> {
    void onSuccess(T t);
    void onFailure(String error);
}
