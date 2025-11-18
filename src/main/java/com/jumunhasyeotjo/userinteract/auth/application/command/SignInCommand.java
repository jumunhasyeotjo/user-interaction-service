package com.jumunhasyeotjo.userinteract.auth.application.command;

public record SignInCommand(
    String name,
    String password
){
}