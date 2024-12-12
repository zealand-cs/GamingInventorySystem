package com.codecrafter.exceptions;

import java.io.IOException;

/**
 * Thrown when a file is formatted incorrectly, e.g. invalid json or wrong types.
 * This should only occur when the file has been modified from the outside.
 */
public class MalformedFileException extends IOException {

}
