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
package board;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

public interface SiteStrategy {
	/**
	 * Check that the given strategy can be used for the given URL. Returns false in case of an error.
	 * @param siteUrl URL the strategy should be used for
	 * @return true if the strategy can be used
	 */
	public boolean validSiteStrategy(URL siteUrl);
	
	/**
	 * Returns a map containing the full name of boards and the 
	 * matching URL. A empty map is returned on error.
	 * @param mainPage document containing the main page
	 * @return a map containing Names and URL's
	 */
	public Map<String, URL> findBoards(Document mainPage);
	
	/**
	 * Returns the number of pages from the board that are available.
	 * Returns 0 on error.
	 * @param boardPage document containing a board page
	 * @return number of accessible pages
	 */
	public int getBoardPageCount(Document boardPage);
	
	/**
	 * Parses the given page and return a list URLs for the contained threads.
	 * Returns a empty list on error.
	 * @param boardPage document containing a board page
	 * @return a list of thread URLs
	 */
	public List<URL> parsePage(Document boardPage);
	
	/**
	 * Parse the given thread and return all posts found.
	 * Returns a empty list on error. 
	 * @param boardThread document containing an imageboard thread
	 * @return a list of found posts
	 */
	public List<Post> parseThread(Document boardThread);
	
	/**
	 * Returns the thread number of the given thread. Returns 0 on error.
	 * @param threadUrl URL of the thread to extract the number from.
	 * @return the number of the thread.
	 */
	public int getThreadNumber(URL threadUrl);
	
	/**
	 * Returns the board shortcut for the given URL. Returns an empty string on error.
	 * @param threadUrl URL of the board to get the shortcut from.
	 * @return the boards shortcut.
	 */
	public String getBoardShortcut(URL threadUrl);
}
