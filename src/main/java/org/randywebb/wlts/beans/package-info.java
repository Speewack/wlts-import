/** "Beans" used to contain information that can be pushed to a CSV file.
    Originally these were actual Java beans (They are serializable, have a zero-argument
    constructor, and allow access to properties using getter and setter methods. See
    https://en.wikipedia.org/wiki/JavaBeans ).
    To simplify the coding, these were changed from strict beans to objects more like JavaScript
    (arbitrary fields may be added at any time).

    See AbstractBean
*/
package org.randywebb.wlts.beans;

