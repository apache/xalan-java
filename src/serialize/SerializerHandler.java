package serialize;


/**
 * Interface that supplements {@link org.xml.sax.DocumentHandler} and
 * {@link org.xml.sax.ContentHandler} with additional methods suitable
 * for serialization. This interface is required only for XML and
 * HTML serializers.
 *
 * @version Alpha
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
public interface SerializerHandler
{


    /**
     * Starts an un-escaping section. All characters printed within an
     * un-escaping section are printed as is, without escaping special
     * characters into entity references. Only XML and HTML serializers
     * need to support this method.
     * <p>
     * The contents of the un-escaping section will be delivered through
     * the regular <tt>characters</tt> event.
     */
    public void startNonEscaping();


    /**
     * Ends an un-escaping section.
     *
     * @see #startNonEscaping
     */
    public void endNonEscaping();


    /**
     * Starts a whitespace preserving section. All characters printed
     * within a preserving section are printed without indentation and
     * without consolidating multiple spaces. This is equivalent to
     * the <tt>xml:space=&quot;preserve&quot;</tt> attribute. Only XML
     * and HTML serializers need to support this method.
     * <p>
     * The contents of the whitespace preserving section will be delivered
     * through the regular <tt>characters</tt> event.
     */
    public void startPreserving();


    /**
     * Ends a whitespace preserving section.
     *
     * @see #startPreserving
     */
    public void endPreserving();


}





