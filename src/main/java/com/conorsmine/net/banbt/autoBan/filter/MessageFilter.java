package com.conorsmine.net.banbt.autoBan.filter;

/**
 * A utils class used to filter the console for the
 * BanItem plugins error msg.
 * Specifically the error msg about the
 * "bannable" tag not being valid.
 */
public interface MessageFilter {

    String ID = " ";    // Adds an invisible char, so the plugin won't try to check it again
    String SEP = "------------------------";
    String DIS = ">> Unknown action data bannable.";

    boolean sendMessage(String msg);
}
