/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.utils;

/**
 * Bare-bones, unsafe, fast string buffer. No thread-safety, no
 * parameter range checking, exposed fields. Note that in typical
 * applications, thread-safety of a StringBuffer is a somewhat 
 * dubious concept in any case.
 * <p>
 * Note that Stree is using a single FastStringBuffer as a string pool,
 * by recording start and length indices within a single buffer. This
 * minimizes heap overhead, but of course requires more work when retrieving
 * the data.
 * <p>
 * This has been recoded to operate as a "chunked buffer". Doing so
 * reduces (or, when initial chunk size equals max chunk size,
 * eliminates) the need to recopy existing information when an append
 * exceeds the space available; we just allocate another chunk and
 * flow across to it. (The array of chunks may need to grow,
 * admittedly, but that's a much smaller object.) Some excess
 * recopying may arise when we extract Strings which cross chunk
 * boundaries; larger chunks make that less frequent.  <p> The size
 * values are parameterized, to allow tuning this code. In theory,
 * RTFs might want to be tuned differently from the main document's
 * text.
 * <p>
 * STATUS: I'm not getting as much performance gain out of this as I'd
 * hoped, nor is the relationship between the tuning parameters and
 * performance particularly intuitive. Under some conditions I do seem
 * to be able to knock up to 19% off the execution time for our
 * largest testcases, which is certainly nontrivial... but that same
 * setting (fixed 1024-byte chunking for both main tree and RTFs)
 * seems to _add_ that much proportional overhead to some of the
 * smaller ones.  We need to understand this better, improve the
 * parameter selection/growth heuristics, and perhaps (if all else
 * fails) consider exposing those parameters for advanced users to
 * fiddle with as suits their needs.  */
public class FastStringBuffer
{
  /** Field m_chunkBits sets our chunking strategy, by saying how many
   * bits of index can be used within a single chunk before flowing over
   * to the next chunk. For example, if m_chunkbits is set to 15, each
   * chunk can contain up to 2^15 (32K) characters  */
  int m_chunkBits=15;
        
  /** Field m_chunkSize establishes the maximum size of one chunk of the array
   * as 2**chunkbits characters.
   * (Which may also be the minimum size if we aren't tuning for storage) */
  int m_chunkSize; // =1<<(m_chunkBits-1);
  
  /** Field m_chunkMask is m_chunkSize-1 -- in other words, m_chunkBits
   * worth of low-order '1' bits, useful for shift-and-mask addressing
   * within the chunks. */
  int m_chunkMask; // =m_chunkSize-1;

  /** Field m_chunkAllocationUnit establishes the initial allocation size for
   * a chunk, and the amount by which the chunk is enlarged. This value is
   * automatically increased for each chunk -- that is, if the first chunk's
   * allocation unit is 1024 characters, the next may allocate in units of
   * 2048 characters, and so on. The intent is that small documents (such as
   * Result Tree Fragments) will minimize memory use at the expense of spending
   * more time recopying data, while large ones will minimize recopying at
   * the expense of wasting more storage due to overallocation.
   * <p>
   * The best compromise between block size, allocation unit size, and
   * rate of growth of allocation units is still to be determined.
   */
  int m_chunkAllocationUnit=1024;

  /** Field m_array holds the string buffer's text contents, using an
   * array-of-arrays. Note that this array, and the arrays it contains, may be 
   * reallocated when necessary in order to allow the buffer to grow; 
   * references to them should be considered to be invalidated after any
   * append. However, the only time these arrays are directly exposed
   * is in the sendSAXcharacters call.
   */
  char[][] m_array;

  /** Field m_lastChunk is an index into m_array[], pointing to the last
   * chunk of the Chunked Array currently in use. Note that additional
   * chunks may actually be allocated, eg if the FastStringBuffer had
   * previously been truncated or if someone issued an ensureSpace request.
   * <p>
   * The insertion point for append operations is addressed by the combination
   * of m_lastChunk and m_firstFree.
   */
  int m_lastChunk = 0;

  /** Field m_firstFree is an index into m_array[m_lastChunk][], pointing to 
   * the first character in the Chunked Array which is not part of the
   * FastStringBuffer's current content. Since m_array[][] is zero-based, 
   * the length of that content can be calculated as 
   * (m_lastChunk<<m_chunkBits) + m_firstFree */
  int m_firstFree = 0;

  /** Field m_lastChunkSize is a cached copy of m_array[m_lastChunk].length 
   */
  int m_lastChunkSize; // = m_initialChunkSize;
  
  /**
   * Construct a FastStringBuffer, with allocation policy as per parameters.
   * <p>
   * For coding convenience, I've expressed both allocation sizes in terms of
   * a number of bits. That's needed for the final size of a chunk,
   * to permit fast and efficient shift-and-mask addressing. It's less critical
   * for the inital size, and may be reconsidered.
   * <p>
   * An alternative would be to accept integer sizes and round to powers of two;
   * that's under consideration.
   * 
   * @param initialChunkBits Length in characters of the initial allocation 
   * of a chunk, expressed in log-base-2. (That is, 10 means allocate 1024 
   * characters.) Later chunks will use larger allocation units, to trade off
   * allocation speed of large document against storage efficiency of small 
   * ones.
   * @param chunkBits Number of character-offset bits that should be used for
   * addressing within a chunk. Maximum length of a chunk is 2^chunkBits 
   * characters.
   */
  public FastStringBuffer(int initialChunkBits,int chunkBits)
  {
    m_array = new char[16][];

    // Don't bite off more than we're prepared to swallow!
    if(initialChunkBits>chunkBits)
      initialChunkBits=chunkBits;
            m_chunkBits=chunkBits;
    m_chunkSize=1<<(chunkBits);
    m_chunkMask=m_chunkSize-1;

    m_lastChunkSize=m_chunkAllocationUnit=1<<(initialChunkBits);
    m_array[0] = new char[m_lastChunkSize];
  }

  /**
   * Construct a FastStringBuffer, using a default allocation policy.
   */
  public FastStringBuffer()
  {
    // 10 bits is 1K. 15 bits is 32K. Remember that these are character
    // counts, so actual memory allocation unit is doubled for UTF-16 chars.
    //
    // For reference: In the original FastStringBuffer, we simply
    // overallocated by blocksize (default 1KB) on each buffer-growth.
    this(10,15);
  }

  /**
   * Construct a FastStringBuffer, using the specified initial unit.
   * Resembles the previous version of this code.
   * <p>
   * ISSUE: Should this be considered initial size, or fixed size?
   * Now configured as initial.
   *
   * @param chunkSize Characters per chunk; will round up to power of 2.
   */
  public FastStringBuffer(int initialAllocationUnit)
  {
    this(initialAllocationUnit, 15);
  }

  /**
   * Get the length of the list. Synonym for length().
   *
   * @return the number of characters in the FastStringBuffer's content.
   */
  public final int size()
  {
        return (m_lastChunk<<m_chunkBits) + m_firstFree;
  }

  /**
   * Get the length of the list. Synonym for size().
   *
   * @return the number of characters in the FastStringBuffer's content.
   */
  public final int length()
  {
    return (m_lastChunk<<m_chunkBits) + m_firstFree;
  }

  /**
   * Discard the content of the FastStringBuffer. Does _not_ release
   * any of the storage space.
   */
  public final void reset()
  {
    m_lastChunk = 0;
    m_firstFree = 0;
  }

  /**
   * Directly set how much of the FastStringBuffer's storage is to be
   * considered part of its content. This is a fast but hazardous
   * operation. It is not protected against negative values, or values
   * greater than the amount of storage currently available... and even
   * if additional storage does exist, its contents are unpredictable.
   * The only safe use for our setLength() is to truncate the FastStringBuffer
   * to a shorter string.
   * <p>
   * QUERY: Given that this operation will be used relatively rarely,
   * does it really need to be so highly optimized?
   *
   * @param l New length. If l<0 or l>=getLength(), this operation will
   * not report an error but future operations will almost certainly fail.
   */
  public final void setLength(int l)
  {
    m_lastChunk = l >>> m_chunkBits;
    m_firstFree = l  &  m_chunkMask;
  }

  /**
   * Note that this operation has been somewhat deoptimized by the shift to a
   * chunked array, as there is no factory method to produce a String object 
   * directly from an array of arrays and hence a double copy is needed.
   * By using ensureCapacity we hope to minimize the heap overhead of building 
   * the intermediate StringBuffer.
   * <p>
   * (It really is a pity that Java didn't design String as a final subclass
   * of MutableString, rather than having StringBuffer be a separate hierarchy.
   * We'd avoid a <strong>lot</strong> of double-buffering.)
   * 
   * @return the contents of the FastStringBuffer as a standard Java string.
   */
  public final String toString()
  {
    return getString(0,0,(m_lastChunk<<m_chunkBits)+m_firstFree);
  }

  /**
   * Append a single character onto the FastStringBuffer, growing the 
   * storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_array[][] may no longer be valid....
   * though in fact they should be in this instance.
   *
   * @param value character to be appended.
   */
  public final void append(char value)
  {
    char[] chunk;
          
    // We may have preallocated chunks. If so, all but last should
    // be at full size.
    boolean lastchunk=(m_lastChunk+1==m_array.length);
          
    if(m_firstFree<m_lastChunkSize) // Simplified test single-character-fits
      chunk=m_array[m_lastChunk];
          
    else if (m_lastChunkSize<m_chunkSize)
      {
        // Grow chunk. Only arises if this is most recent chunk allocated,
        // as earlier ones will already be at full size.
        int newsize=m_lastChunkSize+m_chunkAllocationUnit;
        chunk=new char[newsize];
        System.arraycopy(m_array[m_lastChunk],0,chunk,0,m_firstFree);
        m_array[m_lastChunk]=chunk;
        m_lastChunkSize=newsize;
      }
    else
      {
        // Extend array?
        int i=m_array.length;
        if(m_lastChunk+1==i)
          {
            char[][] newarray=new char[i+16][];
            System.arraycopy(m_array,0,newarray,0,i);
            m_array=newarray;
          }
                  
        // Advance one chunk
        chunk=m_array[++m_lastChunk];
        if(chunk==null)
          {
            // Add a chunk. Allocate bigger pieces this time.
            if(m_chunkAllocationUnit<m_chunkSize)
              m_chunkAllocationUnit<<=1;
            chunk=m_array[m_lastChunk]=new char[m_lastChunkSize=m_chunkAllocationUnit];
          }
        else // Previously allocated
          m_lastChunkSize=chunk.length;
                  
        m_firstFree=0;
      }

    // Space exists in the chunk. Append the character.
    chunk[m_firstFree++]=value;
  }

  /**
   * Append the contents of a String onto the FastStringBuffer, 
   * growing the storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_array[] may no longer be valid.
   *
   * @param value String whose contents are to be appended.
   */
  public final void append(String value)
  {
    int strlen=value.length();
    if (0 == strlen)
      return;
    int copyfrom=0;
    char[] chunk=m_array[m_lastChunk];
    int available=m_lastChunkSize-m_firstFree;
          
    // Repeat while data remains to be copied
    while(strlen>0)
      {
        // Enlarge chunk if necessary/possible (up to maximum)
        int newsize=(
                     (m_firstFree+strlen // needed space
+m_chunkAllocationUnit-1) // round up
                     &
                     ~(m_chunkAllocationUnit-1)); // round off
        if(newsize>m_chunkSize) // Not to exceed maximum.
          newsize=m_chunkSize;

        if(newsize!=m_lastChunkSize) // We're enlarging!
          {
            char[] newchunk=new char[newsize];
            if(chunk!=null)
              System.arraycopy(chunk,0,newchunk,0,m_firstFree);
            chunk=m_array[m_lastChunk]=newchunk;
            m_lastChunkSize=newsize;
            available=newsize-m_firstFree;
          }
                  
        // Copy what fits
        if(available>strlen) available=strlen;
        value.getChars(copyfrom, available, m_array[m_lastChunk], m_firstFree);
        strlen-=available;
        copyfrom+=available;
                  
        // If there's more left, allocate another chunk and continue
        if(strlen>0)
          {
            // Extend array?
            int i=m_array.length;
            if(m_lastChunk+1==i)
              {
                char[][] newarray=new char[i+16][];
                System.arraycopy(m_array,0,newarray,0,i);
                m_array=newarray;
              }
                  
            // Advance one chunk
            chunk=m_array[++m_lastChunk];
            if(chunk==null)
              {
                if(m_chunkAllocationUnit<m_chunkSize)
                  m_chunkAllocationUnit<<=1;
                available=m_lastChunkSize=m_chunkAllocationUnit;
                chunk=m_array[m_lastChunk]=new char[m_lastChunkSize];
              }
            else
              available=m_lastChunkSize=chunk.length;
                          
            m_firstFree=0;
          }
      }
          
    // Adjust the insert point in the last chunk, when we've reached it.
    m_firstFree+=available;
  }

  /**
   * Append the contents of a StringBuffer onto the FastStringBuffer,
   * growing the storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_array[] may no longer be valid.
   *
   * @param value StringBuffer whose contents are to be appended.
   */
  public final void append(StringBuffer value)
  {
    int strlen=value.length();
    if (0 == strlen)
      return;
    int copyfrom=0;
    char[] chunk=m_array[m_lastChunk];
    int available=m_lastChunkSize-m_firstFree;
          
    // Repeat while data remains to be copied
    while(strlen>0)
      {
        // Enlarge chunk if necessary/possible (up to maximum)
        int newsize=(
                     (m_firstFree+strlen // needed space
+m_chunkAllocationUnit-1) // round up
                     &
                     ~(m_chunkAllocationUnit-1)); // round off
        if(newsize>m_chunkSize) // Not to exceed maximum.
          newsize=m_chunkSize;

        if(newsize!=m_lastChunkSize) // We're enlarging!
          {
            char[] newchunk=new char[newsize];
            if(chunk!=null)
              System.arraycopy(chunk,0,newchunk,0,m_firstFree);
            chunk=m_array[m_lastChunk]=newchunk;
            m_lastChunkSize=newsize;
            available=newsize-m_firstFree;
          }
                  
        // Copy what fits
        if(available>strlen) available=strlen;
        value.getChars(copyfrom, available, m_array[m_lastChunk], m_firstFree);
        strlen-=available;
        copyfrom+=available;
                  
        // If there's more left, allocate another chunk and continue
        if(strlen>0)
          {
            // Extend array?
            int i=m_array.length;
            if(m_lastChunk+1==i)
              {
                char[][] newarray=new char[i+16][];
                System.arraycopy(m_array,0,newarray,0,i);
                m_array=newarray;
              }
                  
            // Advance one chunk
            chunk=m_array[++m_lastChunk];
            if(chunk==null)
              {
                if(m_chunkAllocationUnit<m_chunkSize)
                  m_chunkAllocationUnit<<=1;
                available=m_lastChunkSize=m_chunkAllocationUnit;
                chunk=m_array[m_lastChunk]=new char[m_lastChunkSize];
              }
            else
              available=m_lastChunkSize=chunk.length;
                          
            m_firstFree=0;
          }
      }
          
    // Adjust the insert point in the last chunk, when we've reached it.
    m_firstFree+=available;
  }

  /**
   * Append part of the contents of a Character Array onto the 
   * FastStringBuffer,  growing the storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_array[] may no longer be valid.
   *
   * @param chars character array from which data is to be copied
   * @param start offset in chars of first character to be copied,
   * zero-based.
   * @param length number of characters to be copied
   */
  public final void append(char[] chars, int start, int length)
  {
    int strlen=length;
    if (0 == strlen)
      return;
    int copyfrom=start;
    char[] chunk=m_array[m_lastChunk];
    int available=m_lastChunkSize-m_firstFree;
          
    // Repeat while data remains to be copied
    while(strlen>0)
      {
        // Enlarge chunk if necessary/possible (up to maximum)  *****
        int newsize=(
                     (m_firstFree+strlen // needed space
+m_chunkAllocationUnit-1) // round up
                     &
                     ~(m_chunkAllocationUnit-1)); // round off
        if(newsize>m_chunkSize) // Not to exceed maximum.
          newsize=m_chunkSize;

        if(newsize!=m_lastChunkSize) // We're enlarging!
          {
            char[] newchunk=new char[newsize];
            if(chunk!=null)
              System.arraycopy(chunk,0,newchunk,0,m_firstFree);
            chunk=m_array[m_lastChunk]=newchunk;
            m_lastChunkSize=newsize;
            available=newsize-m_firstFree;
          }
                  
        // Copy what fits
        if(available>strlen) available=strlen;
        System.arraycopy(chars,copyfrom, m_array[m_lastChunk], m_firstFree, available);
        strlen-=available;
        copyfrom+=available;
                  
        // If there's more left, allocate another chunk and continue
        if(strlen>0)
          {
            // Extend array?
            int i=m_array.length;
            if(m_lastChunk+1==i)
              {
                char[][] newarray=new char[i+16][];
                System.arraycopy(m_array,0,newarray,0,i);
                m_array=newarray;
              }
                  
            // Advance one chunk
            chunk=m_array[++m_lastChunk];
            if(chunk==null)
              {
                if(m_chunkAllocationUnit<m_chunkSize)
                  m_chunkAllocationUnit<<=1;
                available=m_lastChunkSize=m_chunkAllocationUnit;
                chunk=m_array[m_lastChunk]=new char[m_lastChunkSize];
              }
            else
              available=m_lastChunkSize=chunk.length;
                          
            m_firstFree=0;
          }
      }
          
    // Adjust the insert point in the last chunk, when we've reached it.
    m_firstFree+=available;
  }

  /**
   * Append the contents of another FastStringBuffer onto 
   * this FastStringBuffer, growing the storage if necessary.
   * <p>
   * NOTE THAT after calling append(), previously obtained
   * references to m_array[] may no longer be valid.
   *
   * @param value FastStringBuffer whose contents are
   * to be appended.
   */
  public final void append(FastStringBuffer value)
  {
    // Complicating factor here is that the two buffers may use
    // different chunk sizes, and even if they're the same we're
    // probably on a different alignment due to previously appended
    // data. We have to work through the source in bite-sized chunks.
    int strlen=value.length();
    if (0 == strlen)
      return;
    int copyfrom=0;
    char[] chunk=m_array[m_lastChunk];
    int available=m_lastChunkSize-m_firstFree;
          
    // Repeat while data remains to be copied
    while(strlen>0)
      {
        // Enlarge chunk if necessary/possible (up to maximum)
        int newsize=(
                     (m_firstFree+strlen // needed space
+m_chunkAllocationUnit-1) // round up
                     &
                     ~(m_chunkAllocationUnit-1)); // round off
        if(newsize>m_chunkSize) // Not to exceed maximum.
          newsize=m_chunkSize;

        if(newsize!=m_lastChunkSize) // We're enlarging!
          {
            char[] newchunk=new char[newsize];
            if(chunk!=null)
              System.arraycopy(chunk,0,newchunk,0,m_firstFree);
            chunk=m_array[m_lastChunk]=newchunk;
            m_lastChunkSize=newsize;
            available=newsize-m_firstFree;
          }
                  
        // Copy what fits
        if(available>strlen) available=strlen;
                  
        int sourcechunk=(copyfrom+value.m_chunkSize-1)>>>value.m_chunkBits;
        int sourcecolumn=copyfrom & value.m_chunkMask;
        int runlength=value.m_chunkSize-sourcecolumn;
        if(runlength>available) runlength=available;
        System.arraycopy(value.m_array[sourcechunk],sourcecolumn,
                         m_array[m_lastChunk], m_firstFree, runlength);
        if(runlength!=available)
          System.arraycopy(value.m_array[sourcechunk+1],0,
                           m_array[m_lastChunk], m_firstFree+runlength, available-runlength);
                  
        strlen-=available;
        copyfrom+=available;
                  
        // If there's more left, allocate another chunk and continue
        if(strlen>0)
          {
            // Extend array?
            int i=m_array.length;
            if(m_lastChunk+1==i)
              {
                char[][] newarray=new char[i+16][];
                System.arraycopy(m_array,0,newarray,0,i);
                m_array=newarray;
              }
                  
            // Advance one chunk
            chunk=m_array[++m_lastChunk];
            if(chunk==null)
              {
                if(m_chunkAllocationUnit<m_chunkSize)
                  m_chunkAllocationUnit<<=1;
                available=m_lastChunkSize=m_chunkAllocationUnit;
                chunk=m_array[m_lastChunk]=new char[m_lastChunkSize];
              }
            else
              available=m_lastChunkSize=chunk.length;
                          
            m_firstFree=0;
          }
      }
          
    // Adjust the insert point in the last chunk, when we've reached it.
    m_firstFree+=available;
  }

  /** @return true if the specified range of characters are all whitespace,
   * as defined by XMLCharacterRecognizer.
   * <p>
   * CURRENTLY DOES NOT CHECK FOR OUT-OF-RANGE.
   * 
   * @param start Offset of first character in the range.
   * @param length Number of characters to send.
   */
  public boolean isWhitespace(int start, int length)
  {
    int sourcechunk=start >>> m_chunkBits;
    int sourcecolumn=start & m_chunkMask;
    int available=m_chunkSize-sourcecolumn;
          
    while(length>0)
      {
        int runlength=(length<=available) ? length : available;
        if(!org.apache.xml.utils.XMLCharacterRecognizer.isWhiteSpace(
                                                                     m_array[sourcechunk],sourcecolumn,runlength))
          return false;
        
        length-=runlength;
        ++sourcechunk;
        sourcecolumn=0;
        available=m_chunkSize;
      }
          
    return true;
  }
  
  /** @return a new String object initialized from the specified range of 
   * characters.
   * @param start Offset of first character in the range.
   * @param length Number of characters to send.
   */
  public String getString(int start, int length)
  {
    return getString(start>>>m_chunkBits,start&m_chunkMask,length);
  }
  
  /** Internal support for toString() and getString().
   * 
   * Note that this operation has been somewhat deoptimized by the shift to a
   * chunked array, as there is no factory method to produce a String object 
   * directly from an array of arrays and hence a double copy is needed.
   * By presetting length we hope to minimize the heap overhead of building 
   * the intermediate StringBuffer.
   * <p>
   * (It really is a pity that Java didn't design String as a final subclass
   * of MutableString, rather than having StringBuffer be a separate hierarchy.
   * We'd avoid a <strong>lot</strong> of double-buffering.)
   * 
   * @return the contents of the FastStringBuffer as a standard Java string.
   */
  String getString(int startChunk,int startColumn,int length)
  {
    int stop=(startChunk<<m_chunkBits)+startColumn+length;
    int stopChunk=stop>>>m_chunkBits;
    int stopColumn=stop&m_chunkMask;
          
    StringBuffer sb=new StringBuffer(length);
          
    for(int i=startChunk;i<stopChunk;++i)
      {
        sb.append(m_array[i],startColumn,m_chunkSize-startColumn);
        startColumn=0; // after first chunk
      }

    // Last, or only, chunk
    sb.append(m_array[stopChunk],startColumn,stopColumn-startColumn);
          
    return sb.toString();
  }
  
  /** Sends the specified range of characters as one or more SAX characters()
   * events.
   * Note that the buffer reference passed to the ContentHandler may be 
   * invalidated if the FastStringBuffer is edited; it's the user's 
   * responsibility to manage access to the FastStringBuffer to prevent this
   * problem from arising.
   * <p>
   * Note too that there is no promise that the output will be sent as a
   * single call. As is always true in SAX, one logical string may be split
   * across multiple blocks of memory and hence delivered as several
   * successive events.
   * 
   * @param ch SAX ContentHandler object to receive the event.
   * @param start Offset of first character in the range.
   * @param length Number of characters to send.
   * @exception org.xml.sax.SAXException may be thrown by handler's
   * characters() method.
   */
  public void sendSAXcharacters(org.xml.sax.ContentHandler ch,int start, int length) 
       throws org.xml.sax.SAXException
  {
    int stop=start+length;
    int startChunk=start>>>m_chunkBits;
    int startColumn=start&m_chunkMask;
    int stopChunk=stop>>>m_chunkBits;
    int stopColumn=stop&m_chunkMask;
          
    for(int i=startChunk;i<stopChunk;++i)
      {
        ch.characters(m_array[i],startColumn,m_chunkSize-startColumn);
        startColumn=0; // after first chunk
      }

    // Last, or only, chunk
    ch.characters(m_array[stopChunk],startColumn,stopColumn-startColumn);
  }

}
