package fakestore.controller;

import com.google.gson.JsonObject;

import java.util.function.Function;

public interface IRoute{
    Runnable navigateTo(JsonObject args);
}