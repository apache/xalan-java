/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author G. Todd Miller 
 *
 */

package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.NodeIterator;
import org.apache.xalan.xsltc.TransletException;

import org.apache.xml.dtm.ref.DTMAxisIteratorBase;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMDefaultBase;

public final class DupFilterIterator extends DTMAxisIteratorBase {

    private final static int INIT_DATA_SIZE = 16;

    private final DTMAxisIterator _source; // the source iterator
    private int[] _data = null;         // cached nodes from the source
    private int _last = 0;              // the number of nodes in this iterator
    private int _current = 0;

    /**
     * Creates a new duplicate filter iterator based on an existing iterator.
     * This iterator should be used with union expressions and other complex
     * iterator combinations (like 'get me the parents of all child node in
     * the dom' sort of thing). The iterator is also used to cache node-sets
     * returned by id() and key() iterators.
     * @param source The iterator this iterator will get its nodes from
     */
    public DupFilterIterator(DTMAxisIterator source) {
	// Save a reference to the source iterator
	_source = source;

	// Cache contents of id() or key() index right away. Necessary for
	// union expressions containing multiple calls to the same index, and
	// correct as well since start-node is irrelevant for id()/key() exrp.
	if (source instanceof KeyIndex) setStartNode(DTMDefaultBase.ROOTNODE);
    }

    /**
     * Returns the next node in this iterator - excludes duplicates.
     * @return The next node in this iterator
     */
    public int next() {
	return _current < _last ? _data[_current++] : END;
    }

    /**
     * Set the start node for this iterator
     * @param node The start node
     * @return A reference to this node iterator
     */
    public DTMAxisIterator setStartNode(int node) {

	int i, j; // loop variables declared first for speed - don't move!!!

	// KeyIndex iterators are always relative to the root node, so there
	// is never any point in re-reading the iterator (and we SHOULD NOT).
	if ((_source instanceof KeyIndex) && (_data != null)) {
            return this;
        }

	// If the _data array is populated, and the current start node is
	// equal to the new start node, we know we already have what we need.
	if ((_data == null) || (node != _startNode)) {
            _startNode = node;
            _source.setStartNode(node);
            int[] data = new int[INIT_DATA_SIZE];
            int sourceNodeCount = 0;

	    // Gather all nodes from the source iterator
            while ((node = _source.next()) != END) {
                if (sourceNodeCount == data.length) {
                    int[] newArray = new int[data.length * 2];
                    System.arraycopy(data, 0, newArray, 0, sourceNodeCount);
                    data = newArray;
                }
                data[sourceNodeCount++] = node;
            }

            // %REVIEW%:  Is this the best approach?  Code used to keep nodes
            // in sorted order as they were retrieved from _source.next(),
            // inserting at appropriate point at each step, eliminating
            // duplicates.  That was a win when there were relatively few
            // unique nodes.  When there were very many, the insertions
            // became very expensive.  Perhaps we could use that approach
            // while the number of nodes is small, and then switch to sorting
            // after the fact when it reaches a threshold.

            // Factor out the trivial case to avoid overhead of sort
            if (sourceNodeCount > 1) {
                // Sort source nodes using merge sort:  Merge two sorted
                // subranges of the array into new sorted ranges, beginning
                // with trivially sorted subranges of size 1.
                int[] mergeArray = new int[sourceNodeCount];

                int doubleMergeSize = 1;
                for (int mergeSize = 1;
                     mergeSize < sourceNodeCount;
                     mergeSize = doubleMergeSize) {
                    int mLow = 0;
                    doubleMergeSize = mergeSize + mergeSize;

                    // Merge adjacent subranges of the array of appropriate size
                    for (int r1Low = 0;
                         r1Low < sourceNodeCount;
                         r1Low = r1Low + doubleMergeSize) {
                        final int r2Low = r1Low + mergeSize;
                        final int numElemsInSecondSet =
                                    Math.min(mergeSize, sourceNodeCount-r2Low);
                        merge(data, r1Low, mergeSize,
                              data, r2Low, numElemsInSecondSet,
                              mergeArray, mLow);
                        mLow = mLow + mergeSize + numElemsInSecondSet;
                    }

                    // Now switch the arrays to double the merger ranges
                    int[] tempArr = mergeArray;
                    mergeArray = data;
                    data = tempArr;
                }

                // Sweep through to see whether there are any duplicates
                for (i = 0;
                     i < sourceNodeCount-1 && data[i] != data[i+1];
                     i++);

                // If any duplicates were found, start compacting them out
                if (i < sourceNodeCount-1) {
                    int nextUniqueIdx = i+1;
                    for (j = i+2; j < sourceNodeCount; j++) {
                        if (data[nextUniqueIdx-1] != data[j]) {
                            data[nextUniqueIdx++] = data[j];
                        }
                    }
                    sourceNodeCount = nextUniqueIdx;
                }
            }
            _last = sourceNodeCount;
            _data = data;
	}

	_current = 0;  // set to beginning 
	return this;
    }

    /**
     *
     * Merge two sorted subranges of arrays into a target array.  The resulting
     * elements in the target array will be in sorted order.
     *
     * @param a An array containing the first sorted subrange
     * @param aLow The starting index for the first array's subrange
     * @param aCount The number of elements in the first array's subrange
     * @param b An array containing the second sorted subrange
     * @param bLow The starting index for the second array's subrange
     * @param bCount The number of elements in the second array's subrange
     * @param t The target array which will contain the two merged subranges
     * @param tLow The starting index in the target array for the merged result
     *
     */
    private static void merge(int[] a, int aLow, int aCount,
                              int[] b, int bLow, int bCount,
                              int[] t, int tLow) {
        int aHigh = aLow + aCount - 1;
        int bHigh = bLow + bCount - 1;
        int tHigh = tLow + aCount + bCount - 1;

        for (int i = tLow; i <= tHigh; i++) {
            if (aLow > aHigh) {
                for (int j = i; j <= tHigh; j++) {
                    t[j] = b[bLow++];
                }
                break;
            } else if (bLow > bHigh) {
                for (int j = i; j <= tHigh; j++) {
                    t[j] = a[aLow++];
                }
                break;
            }

            t[i] = (a[aLow] < b[bLow]) ? a[aLow++] : b[bLow++];
        }
    }

    /**
     * Returns the current position of the iterator. The position is within the
     * node set covered by this iterator, not within the DOM.
     */
    public int getPosition() {
	return (_current);
    }

    /**
     * Returns the position of the last node in this iterator. The integer
     * returned is equivalent to the number of nodes in this iterator.
     */
    public int getLast() {
	return _last;
    }

    /**
     * Saves the position of this iterator - see gotoMark()
     */
    public void setMark() {
	_source.setMark();
	_markedNode = _current;
    }

    /**
     * Restores the position of this iterator - see setMark()
     */
    public void gotoMark() {
	_source.gotoMark();
	_current = _markedNode;
    }

    public DTMAxisIterator reset() {
	_current = 0;
	return(this);
    }

}
