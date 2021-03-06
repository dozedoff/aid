/*  Copyright (C) 2012  Nicholas Wright
	
	part of 'Aid', an imageboard downloader.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io;

import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.dozedoff.commonj.io.DBsettings;

public class SchemaUpdaterTest {
	AidDAO sql;
	Properties local;

	@Before
	public void setUp() throws Exception {
		sql = mock(AidDAO.class);
		local = new Properties();
		local.put(DBsettings.SchemaVersion.toString(), "2");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test(expected=SchemaUpdateException.class)
	public void testInvalidLocal() throws SchemaUpdateException {
		local = new Properties();
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("1");
		SchemaUpdater.update(sql, local);
	}

	@Test(expected=SchemaUpdateException.class)
	public void testInvalidVersion1() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("-1");
		SchemaUpdater.update(sql, local);
	}
	
	@Test(expected=SchemaUpdateException.class)
	public void testInvalidVersion2() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("0");
		SchemaUpdater.update(sql, local);
	}
	
	@Test(expected=SchemaUpdateException.class)
	public void testInvalidVersion3() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("a");
		SchemaUpdater.update(sql, local);
	}
	
	@Test
	public void testUpdate() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("1");
		when(sql.batchExecute((String[]) anyVararg())).thenReturn(true);
		
		SchemaUpdater.update(sql, local);
		verify(sql,times(1)).batchExecute((String[]) anyVararg());
	}
	
	@Test
	public void testNoUpdate() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("2");
		SchemaUpdater.update(sql, local);
		verify(sql,times(0)).batchExecute(null);
	}
	
	@Test(expected=SchemaUpdateException.class)
	public void testRemoteIsNewer() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("3");
		SchemaUpdater.update(sql, local);
	}

}
