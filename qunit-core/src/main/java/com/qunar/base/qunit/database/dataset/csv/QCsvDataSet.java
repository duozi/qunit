/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.qunar.base.qunit.database.dataset.csv;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;

import java.io.File;

/**
 * This class constructs an IDataSet given a directory containing CSV
 * files. It handles translations of "null"(the string), into null. 
 *
 * @author Lenny Marks (lenny@aps.org)
 * @author Last changed by: $Author: changjiang.tang $
 * @version $Revision: 8999 $ $Date: 2012-11-01 16:25:08 +0800 (Thu, 01 Nov 2012) $
 * @since Sep 12, 2004 (pre 2.3)
 */
public class QCsvDataSet extends CachedDataSet {

    public QCsvDataSet(File csvFile) throws DataSetException {
        super(new QCsvProducer(csvFile.getAbsolutePath()));
    }

}
