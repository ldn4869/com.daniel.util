package com.daniel.java.io;


import java.io.File;


/**
 * Unicode-aware FileSystem for Windows NT/2000.
 *
 * @author Konstantin Kladko
 * @since 1.4
 */
class WinNTFileSystem {

    public native String[] list(File f);

    public native long getLength(File f);

    public native int getBooleanAttributes(File f);


    private static native void initIDs();

    static {
        initIDs();
    }
}
