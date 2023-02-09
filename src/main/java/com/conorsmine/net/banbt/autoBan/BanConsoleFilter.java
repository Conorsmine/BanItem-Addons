package com.conorsmine.net.banbt.autoBan;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

import java.util.LinkedList;
import java.util.List;

/**
 * A utils class used to filter the console for the
 * BanItem plugins error msg.
 * Specifically the error msg about the
 * "bannable" tag not being valid.
 */
public class BanConsoleFilter implements Filter {

    // I hate that I gotta do it like this
    private boolean foundErrMsg = false;     // represents if currently logging err msg
    private boolean discard = false;         // If true; delete the cache
    private final List<String> cachedStrings = new LinkedList<>();

    private final Logger logger;

    public BanConsoleFilter(Logger logger) {
        this.logger = logger;
    }

    static final String SEP = "------------------------";
    private Result checkMessage(final String message) {
        if (message.equals("HERE")) return Result.NEUTRAL;
        if (message.equals(">> Unknown action data bannable.")) discard = true;
        if (message.equals(SEP)) foundErrMsg = !foundErrMsg;
        if (foundErrMsg) cachedStrings.add(message);

        // End of Err msg
        if (!foundErrMsg && cachedStrings.size() > 1) {
            if (discard) {
                System.out.println("HERE");
                cachedStrings.clear();
                discard = false;
                foundErrMsg = false;
                return Result.DENY;
            }
            else {
                cachedStrings.forEach(logger::info);
                discard = false;
                foundErrMsg = false;
                return Result.NEUTRAL;
            }
        }

        return (foundErrMsg) ? Result.DENY : Result.NEUTRAL;
    }

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String msg, final Object... params) {
        return this.checkMessage(msg);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0,
                         final Object p1) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0, final Object p1,
                         final Object p2) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0, final Object p1,
                         final Object p2, final Object p3) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0, final Object p1,
                         final Object p2, final Object p3, final Object p4) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0, final Object p1,
                         final Object p2, final Object p3, final Object p4,
                         final Object p5) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0, final Object p1,
                         final Object p2, final Object p3, final Object p4,
                         final Object p5, final Object p6) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0, final Object p1,
                         final Object p2, final Object p3, final Object p4,
                         final Object p5, final Object p6, final Object p7) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0, final Object p1,
                         final Object p2, final Object p3, final Object p4,
                         final Object p5, final Object p6, final Object p7, final Object p8) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String message, final Object p0, final Object p1,
                         final Object p2, final Object p3, final Object p4,
                         final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return this.checkMessage(message);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object msg, final Throwable t) {
        return this.checkMessage(msg.toString());
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Message msg, final Throwable t) {
        return this.checkMessage(msg.getFormattedMessage());
    }

    @Override
    public Result filter(final LogEvent event) {
        return this.checkMessage(event.getMessage().getFormattedMessage());
    }

    @Override
    public State getState() {
        return State.STARTED;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
