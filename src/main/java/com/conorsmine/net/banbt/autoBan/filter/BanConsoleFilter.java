package com.conorsmine.net.banbt.autoBan.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;


public class BanConsoleFilter implements Filter {

    private final MessageFilter filter;

    public BanConsoleFilter(Logger logger) {
        this.filter = new ConsoleMessageFilter(logger);
    }

    private Result checkMessage(final String message) {
        return (filter.sendMessage(message)) ? Result.NEUTRAL : Result.DENY;
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
