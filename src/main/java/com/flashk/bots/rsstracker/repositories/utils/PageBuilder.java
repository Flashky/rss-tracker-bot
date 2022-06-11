package com.flashk.bots.rsstracker.repositories.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Helper PageBuilder that allows pagination a list of elements.
 *
 * @param <T>
 */
public class PageBuilder<T> {

	private List<T> fullContent;
	private Pageable pageable;
	
	/**
	 * Creates <code>PageBuilder</code> with the starting <code>fullContent</code> list.
	 * <p>This resulting <code>Page</code> will have a filtered version from this list depending on the <code>page</code> and <code>size</code> settings.
	 * @param fullContent elements to include.
	 */
	public PageBuilder(List<T> fullContent) {
		this.fullContent = new ArrayList<>(fullContent);
		this.pageable = Pageable.unpaged();
	}
	
	/**
	 * Prepares the page for the first page (page number <code>0</code>) given <code>pageSize</code>.
	 * @param pageSize the size of the page to be returned, must be greater than 0.
	 * @return the page builder.
	 */
	public PageBuilder<T> ofSize(int pageSize) {
		this.pageable = PageRequest.ofSize(pageSize);
		return this;
	}
	
	/**
	 * Prepares the page for the selected given <code>page</code> and <code>pageSize.</code>
	 * @param page zero-based page index, must not be negative.
	 * @param size the size of the page to be returned, must be greater than 0.
	 * @return the page builder.
	 */
	public PageBuilder<T> of(int page, int size) {
		this.pageable = PageRequest.of(page, size);
		return this;
	}
	
	/**
	 * Prepares the page given the <code>pageable</code>.
	 * @param pageable a pageable object, it can either paged or unpaged.
	 * @return the page builder.
	 */
	public PageBuilder<T> of(Pageable pageable) {
		this.pageable = pageable;
		return this;
	}
	
	/**
	 * Builds a <code>Page</code> of elements based on the builder parameters.
	 * @return a page.
	 * @see org.springframework.data.domain.Page Page
	 */
	public Page<T> build() {
		
		Page<T> page;
		
		if(this.pageable.isPaged()) {
			
			long skipNumber = pageable.getPageNumber() * pageable.getPageSize();
			
			List<T> pageContent = fullContent.stream().skip(skipNumber).limit(pageable.getPageSize()).collect(Collectors.toList()); 
			page = new PageImpl<>(pageContent, pageable, fullContent.size());
			
		} else {
			page = new PageImpl<>(fullContent);
		}
		
		return page;
		
	}
}
