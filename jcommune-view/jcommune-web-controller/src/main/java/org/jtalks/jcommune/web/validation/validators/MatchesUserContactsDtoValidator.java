/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.web.validation.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.jtalks.jcommune.model.entity.UserContactType;
import org.jtalks.jcommune.service.UserContactsService;
import org.jtalks.jcommune.web.validation.annotations.MatchesUserContacts;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Validator for {@link MatchesUserContacts}. Checks that one string property 
 * matches regular expression stored in other string property.
 *
 * @author Vyacheslav Mishcheryakov
 * @see MatchesUserContacts
 */
public class MatchesUserContactsDtoValidator implements ConstraintValidator<MatchesUserContacts, Object>{

	private String propertyToValidate;
	private String pathToTypeId;
	private String fieldValue;
	private String pattern;
	
	
	private UserContactsService contactsService;
	
	
	/**
	 * @param contactsService the contactsService to set
	 */
	@Autowired
	public void setContactsService(UserContactsService contactsService) {
		this.contactsService = contactsService;
	}

	/**
     * Initialize validator fields from annotation instance.
     *
     * @param constraintAnnotation {@link MatchesUserContacts} annotation from class
     * @see MatchesUserContacts
     */
	@Override
	public void initialize(MatchesUserContacts constraintAnnotation) {
		this.propertyToValidate = constraintAnnotation.field();
		this.pathToTypeId = constraintAnnotation.storedTypeId();
		
	}

	/**
     * Validate object with {@link MatchesUserContacts} annotation.
     *
     * @param value   object with {@link MatchesUserContacts} annotation
     * @param context validation context
     * @return {@code true} if validation successful or false if fails
     */
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		fetchDataForValidation(value);
		boolean result;
		if (pattern == null) {
			result = true;
		} else if (fieldValue == null) {
			result = false;
		} else {
			result = fieldValue.matches(pattern);
		}
		return result;
	}
	
	/**
     * Retrieving necessary fields from object.
     * Throws {@code IllegalStateException} if field not found.
     *
     * @param value object from which we take values ​​of fields
     */
	private void fetchDataForValidation(Object value) {
        try {
            fieldValue = BeanUtils.getProperty(value, propertyToValidate);
            long typeId = Long.parseLong(BeanUtils.getProperty(value, pathToTypeId));
            UserContactType type = contactsService.get(typeId);
            pattern = type.getValidationPattern();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
