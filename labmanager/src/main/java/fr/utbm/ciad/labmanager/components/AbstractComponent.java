/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.components;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import org.arakhne.afc.vmutil.FileSystem;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Abstract implementation of a JEE component.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractComponent implements Serializable {

    private static final long serialVersionUID = 5194545834081292734L;

    private static final String UNSET_STR = "-"; //$NON-NLS-1$

    /**
     * Factory of URI builder.
     */
    protected final UriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
    private final Random random = new Random();
    /**
     * Constants of the application.
     */
    private ConfigurationConstants constants;
    private MessageSourceAccessor messages;
    @Value("${labmanager.init.data-source")
    private String dataSource;

    /**
     * Constructor.
     *
     * @param messages  the provider of messages.
     * @param constants the accessor to the constants.
     */
    public AbstractComponent(MessageSourceAccessor messages, ConfigurationConstants constants) {
        this.messages = messages;
        this.constants = constants;
    }

    /**
     * Get the value from the given map for an attribute with the given name.
     * <p>This function generates an exception if the value is {@code null} or empty.
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @return the value
     * @see #optionalString(Map, String)
     */
    protected static String ensureString(Map<String, ?> attributes, String name) {
        final var param = inString(attributes.get(name));
        if (param == null) {
            throw new IllegalArgumentException("Missed string parameter: " + name); //$NON-NLS-1$
        }
        return param;
    }

    /**
     * Get the value from the given map for an attribute with the given name.
     * <p>This function does not generate an exception if the value is {@code null} or empty.
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @return the value
     * @see #ensureString(Map, String)
     */
    protected static String optionalString(Map<String, ?> attributes, String name) {
        final var param = inString(attributes.get(name));
        if (Strings.isNullOrEmpty(param)) {
            return null;
        }
        return param;
    }

    /**
     * Get the value from the given map for an attribute with the given name.
     * <p>This function does not generate an exception if the value is {@code null} or empty.
     * <p>If the string value is equal to {@code -}, the function replies {@code null}
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @return the value
     * @see #optionalString(Map, String)
     */
    protected static String optionalStringWithUnsetConstant(Map<String, ?> attributes, String name) {
        final var param = inString(attributes.get(name));
        if (Strings.isNullOrEmpty(param) || UNSET_STR.equals(param)) {
            return null;
        }
        return param;
    }

    /**
     * Get the value from the given map for an attribute with the given name.
     * <p>This function does not generate an exception if the value is {@code null} or empty.
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @return the value
     * @see #ensureString(Map, String)
     */
    protected static boolean optionalBoolean(Map<String, ?> attributes, String name) {
        final var param = inString(attributes.get(name));
        if (param == null) {
            return false;
        }
        try {
            return Boolean.parseBoolean(param);
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Get the value from the given map for an attribute with the given name.
     * <p>This function does not generate an exception if the value is {@code null} or empty.
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @return the value or {@link Float#NaN}.
     * @see #ensureString(Map, String)
     */
    protected static float optionalFloat(Map<String, ?> attributes, String name) {
        final var param = inString(attributes.get(name));
        if (Strings.isNullOrEmpty(param)) {
            return Float.NaN;
        }
        try {
            return Float.parseFloat(param);
        } catch (Throwable ex) {
            return Float.NaN;
        }
    }

    /**
     * Get the value from the given map for an attribute with the given name.
     * <p>This function does not generate an exception if the value is {@code null} or empty.
     *
     * @param attributes   the set of attributes
     * @param name         the name to search for.
     * @param defaultValue the default value.
     * @return the value or {@link Float#NaN}.
     * @see #ensureString(Map, String)
     * @since 3.6
     */
    protected static int optionalInt(Map<String, ?> attributes, String name, int defaultValue) {
        final var param = inString(attributes.get(name));
        if (Strings.isNullOrEmpty(param)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(param);
        } catch (Throwable ex) {
            return defaultValue;
        }
    }

    /**
     * Get the value from the given map for an attribute with the given name.
     * <p>This function does not generate an exception if the value is {@code null} or empty.
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @param type       the type of the enum.
     * @return the value or {@link Float#NaN}.
     * @see #ensureString(Map, String)
     */
    protected static <E extends Enum<E>> E optionalEnum(Map<String, ?> attributes, String name, Class<E> type) {
        final var param = inString(attributes.get(name));
        if (!Strings.isNullOrEmpty(param)) {
            for (final var enumConstant : type.getEnumConstants()) {
                if (enumConstant.name().equalsIgnoreCase(param)) {
                    return enumConstant;
                }
            }
        }
        return null;
    }

    /**
     * Get the local date value from the given map for an attribute with the given name.
     * The attribute must follow one of the formats: {@code YYYY-MM-DD}, {@code YYYY-MD}.
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @return the value
     */
    protected static LocalDate optionalDate(Map<String, ?> attributes, String name) {
        final var dateStr = optionalString(attributes, name);
        LocalDate date = null;
        if (!Strings.isNullOrEmpty(dateStr)) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Throwable ex0) {
                // Test if the date is only "YYYY-MM"
                try {
                    date = LocalDate.parse(dateStr + "-01"); //$NON-NLS-1$
                } catch (Throwable ex1) {
                    date = null;
                }
            }
        }
        return date;
    }

    /**
     * Get the year value from the given map for an attribute with the given name.
     * The attribute must follow one of the formats: {@code YYYY-MM-DD}, {@code YYYY-MD}, or {@code YYYY}.
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @return the value
     */
    protected static int ensureYear(Map<String, ?> attributes, String name) {
        final var dateStr = ensureString(attributes, name);
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Throwable ex0) {
            // Test if the date is only "YYYY-MM"
            try {
                date = LocalDate.parse(dateStr + "-01"); //$NON-NLS-1$
            } catch (Throwable ex1) {
                // Test if the date is only "YYYY"
                try {
                    date = LocalDate.parse(dateStr + "-01-01"); //$NON-NLS-1$
                } catch (Throwable ex2) {
                    date = null;
                }
            }
        }
        if (date == null) {
            throw new IllegalArgumentException("Invalid date parameter: " + name); //$NON-NLS-1$
        }
        return date.getYear();
    }

    /**
     * Get the integer value from the given map for an attribute with the given name.
     *
     * @param attributes the set of attributes
     * @param name       the name to search for.
     * @return the value
     */
    protected static int ensureInt(Map<String, ?> attributes, String name) {
        final var intStr = ensureString(attributes, name);
        try {
            return Integer.parseInt(intStr);
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Find the journal object that corresponds to the given identifier or name.
     *
     * @param journal        the identifier or the name of the journal.
     * @param journalService the service that must be used for accessing the journal's object.
     * @return the journal or {@code null}.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    protected static Journal getJournalWith(String journal, JournalService journalService) {
        if (!Strings.isNullOrEmpty(journal)) {
            try {
                final var id = Integer.parseInt(journal);
                final var journalObj = journalService.getJournalById(id);
                if (journalObj != null) {
                    return journalObj;
                }
            } catch (Throwable ex) {
                //
            }
            final var journalObj = journalService.getJournalByName(journal);
            return journalObj;
        }
        return null;
    }

    /**
     * Find the conference object that corresponds to the given identifier or name.
     *
     * @param conference        the identifier or the name of the conference.
     * @param conferenceService the service that must be used for accessing the conference's object.
     * @return the conference or {@code null}.
     * @Deprecated no replacement.
     * @since 3.6
     */
    @Deprecated(since = "4.0", forRemoval = true)
    protected static Conference getConferenceWith(String conference, ConferenceService conferenceService) {
        if (!Strings.isNullOrEmpty(conference)) {
            try {
                final var id = Integer.parseInt(conference);
                final var conferenceObj = conferenceService.getConferenceById(id);
                if (conferenceObj != null) {
                    return conferenceObj;
                }
            } catch (Throwable ex) {
                //
            }
            final var conferenceObj = conferenceService.getConferenceByName(conference);
            return conferenceObj;
        }
        return null;
    }

    /**
     * Find the person object that corresponds to the given identifier or name.
     *
     * @param dbId          the database identifier of the person. You should provide one of {@code dbId}, {@code webId} or {@code name}.
     * @param webId         the identifier of the webpage of the person. You should provide one of {@code dbId}, {@code webId} or {@code name}.
     * @param name          the name of the person. You should provide one of {@code dbId}, {@code webId} or {@code name}.
     * @param personService the service that must be used for accessing the person's object.
     * @param nameParser    the parser of person names to be used for extracting first and last names.
     * @return the person or {@code null}.
     */
    protected static Person getPersonWith(
            Integer dbId, String webId, String name,
            PersonService personService, PersonNameParser nameParser) {
        if (dbId != null && dbId.intValue() != 0) {
            return personService.getPersonById(dbId.intValue());
        }
        if (!Strings.isNullOrEmpty(webId)) {
            return personService.getPersonByWebPageId(webId);
        }
        if (!Strings.isNullOrEmpty(name)) {
            final var firstName = nameParser.parseFirstName(name);
            final var lastName = nameParser.parseLastName(name);
            final var personObj = personService.getPersonBySimilarName(firstName, lastName);
            return personObj;
        }
        return null;
    }

    /**
     * Find the organization object that corresponds to the given identifier, acronym or name.
     *
     * @param organization        the identifier, acronym or the name of the organization.
     * @param organizationService the service that permits to access to the organization object.
     * @return the organization or {@code null}.
     */
    protected static ResearchOrganization getOrganizationWith(String organization, ResearchOrganizationService organizationService) {
        if (!Strings.isNullOrEmpty(organization)) {
            try {
                final var id = Integer.parseInt(organization);
                final var organizationObj = organizationService.getResearchOrganizationById(id);
                if (organizationObj.isPresent()) {
                    return organizationObj.get();
                }
            } catch (Throwable ex) {
                //
            }
            final var organizationObj = organizationService.getResearchOrganizationByAcronymOrName(organization);
            if (organizationObj.isPresent()) {
                return organizationObj.get();
            }
        }
        return null;
    }

    /**
     * Find the organization object that corresponds to the given identifier, acronym or name.
     *
     * @param identifier          the identifier of the organization. It could be {@code null}.
     * @param name                the acronym or the name of the organization. It could be {@code null}.
     * @param organizationService the service that permits to access to the organization object.
     * @return the organization or {@code null}.
     * @Deprecated no replacement.
     * @since 3.2
     */
    @Deprecated(since = "4.0", forRemoval = true)
    protected static ResearchOrganization getOrganizationWith(Integer dbId, String name, ResearchOrganizationService organizationService) {
        if (dbId != null && dbId.intValue() != 0) {
            final var organizationOpt = organizationService.getResearchOrganizationById(dbId.intValue());
            if (organizationOpt.isEmpty()) {
                return null;
            }
            return organizationOpt.get();
        }
        final var inOrganizationAcronym = inString(name);
        if (!Strings.isNullOrEmpty(inOrganizationAcronym)) {
            final var organizationOpt = organizationService.getResearchOrganizationByAcronymOrName(name);
            if (organizationOpt.isEmpty()) {
                return null;
            }
            return organizationOpt.get();
        }
        return null;
    }

    /**
     * Clean and normalized a string that is provided as input to an endpoint.
     *
     * @param input the input string to the endpoint.
     * @return the normalized string that is equivalent to the argument.
     */
    public static String inString(Object input) {
        final var strInput = input == null ? null : input.toString();
        var out = Strings.emptyToNull(strInput);
        if (out != null) {
            out = out.trim();
            out = Strings.emptyToNull(out);
        }
        return out;
    }

    /**
     * Clean and normalized an URL that is provided as input to an endpoint.
     *
     * @param input the input string to the endpoint.
     * @return the normalized URL that is equivalent to the argument.
     * @since 3.4
     */
    public static URL inURL(Object input) {
        final var strInput = inString(input);
        if (!Strings.isNullOrEmpty(strInput)) {
            try {
                return new URI(strInput).toURL();
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Clean and normalized a float that is provided as input to an endpoint.
     *
     * @param input the input string to the endpoint.
     * @return the float that is equivalent to the argument, or {@code null} if the string cannot be converted to a float.
     * @since 3.0
     */
    public static Float inFloat(Object input) {
        final var strInput = inString(input);
        if (!Strings.isNullOrEmpty(strInput)) {
            try {
                return Float.valueOf(strInput);
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Clean and normalized a integer that is provided as input to an endpoint.
     *
     * @param input the input string to the endpoint.
     * @return the integer that is equivalent to the argument, or {@code null} if the string cannot be converted to an integer.
     * @since 3.4
     */
    public static Integer inInteger(Object input) {
        final var strInput = inString(input);
        if (!Strings.isNullOrEmpty(strInput)) {
            try {
                return Integer.valueOf(strInput);
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Clean and normalized an enumeration item that is provided as input to an endpoint.
     *
     * @param <E>          the type of the enumeration.
     * @param input        the input string to the endpoint.
     * @param type         the type of the enumeration.
     * @param defaultValue the default value.
     * @return the enumeration item that is equivalent to the argument, or the default value if the string
     * cannot be converted to an enumeration item.
     * @since 3.6
     */
    public static <E extends Enum<E>> E inEnum(Object input, Class<E> type, E defaultValue) {
        final var param = inString(input);
        if (!Strings.isNullOrEmpty(param)) {
            for (final var enumConstant : type.getEnumConstants()) {
                if (enumConstant.name().equalsIgnoreCase(param)) {
                    return enumConstant;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Clean and normalized a integer that is provided as input to an endpoint.
     *
     * @param input        the input string to the endpoint.
     * @param defaultValue the default value.
     * @return the integer that is equivalent to the argument, or the default value.
     * @since 3.4
     */
    public static int inInt(Object input, int defaultValue) {
        final var value = inInteger(input);
        if (value == null) {
            return defaultValue;
        }
        return value.intValue();
    }

    private static Person extractPerson(String personDesc, Pattern idPattern, boolean createPerson,
                                        PersonService personService, PersonNameParser nameParser, Logger logger) {
        Person person = null;
        var personId = 0L;
        if (idPattern.matcher(personDesc).matches()) {
            // Numeric value means that the person is known.
            try {
                personId = Integer.parseInt(personDesc);
            } catch (Throwable ex) {
                // Silent
            }
        }
        if (personId == 0) {
            // The person seems to be not in the database already. Check it based on the name.
            final var firstName = nameParser.parseFirstName(personDesc);
            final var lastName = nameParser.parseLastName(personDesc);
            personId = personService.getPersonIdByName(firstName, lastName);
            if (personId == 0) {
                // Now, it is sure that the person is unknown
                if (createPerson) {
                    person = personService.createPerson(logger, firstName, lastName);
                    logger.info("New person \"" + personDesc + "\" created with id: " + person.getId()); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } else {
                person = personService.getPersonById(personId);
                if (person == null) {
                    throw new IllegalArgumentException("Unknown person with id: " + personId); //$NON-NLS-1$
                }
            }
        } else {
            // Check if the given author identifier corresponds to a known person.
            person = personService.getPersonById(personId);
            if (person == null) {
                throw new IllegalArgumentException("Unknown person with id: " + personId); //$NON-NLS-1$
            }
        }
        return person;
    }

    /**
     * Replies the message provider of this controller.
     *
     * @return the accessor.
     */
    public MessageSourceAccessor getMessageSourceAccessor() {
        return this.messages;
    }

    /**
     * Change the message provider of this controller.
     *
     * @param accessor the accessor.
     */
    public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
        this.messages = accessor;
    }

    /**
     * Short-hand function for {@link #getMessageSourceAccessor()} and {@link MessageSourceAccessor#getMessage(String, Object[])}.
     *
     * @param locale the locale to be used.
     * @param key    the message id.
     * @param args   the arguments to inject in the string.
     * @return the string.
     */
    public String getMessage(Locale locale, String key, Object... args) {
        return getMessageSourceAccessor().getMessage(key, args, locale);
    }

    /**
     * Generate an UUID.
     *
     * @return the UUID.
     */
    protected Integer generateUUID() {
        return Integer.valueOf(Math.abs(this.random.nextInt()));
    }

    /**
     * Replies the constants associated to this application.
     *
     * @return the constants.
     * @since 4.0
     */
    public ConfigurationConstants getApplicationConstants() {
        if (this.constants == null) {
            this.constants = new ConfigurationConstants();
        }
        return this.constants;
    }

    /**
     * Loop on the persons that are described in the provided list.
     * This list may contain the identifiers of the persons or the full names of the persons.
     *
     * @param persons       the list of identifiers or full names.
     * @param createPerson  indicates if any person that is not yet stored in the database must be created on the fly.
     * @param personService the service for accessing the persons in the database.
     * @param nameParser    the parser of person names.
     * @param logger        the logger to use for put a message in the log.
     * @param consumer      invoked for each person in the list.
     */
    @SuppressWarnings("static-method")
    protected void forEarchPerson(List<String> persons, boolean createPerson,
                                  PersonService personService, PersonNameParser nameParser, Logger logger, Consumer<Person> consumer) {
        final var idPattern = Pattern.compile("\\d+"); //$NON-NLS-1$
        for (final var personDesc : persons) {
            final var person = extractPerson(personDesc, idPattern, createPerson, personService, nameParser, logger);
            if (person != null) {
                consumer.accept(person);
            }
        }
    }

    /**
     * Extract a person from the description.
     * This description may contain the identifier of the person or the full name of the person.
     *
     * @param personDesc    the identifier or full name.
     * @param createPerson  indicates if any person that is not yet stored in the database must be created on the fly.
     * @param personService the service for accessing the persons in the database.
     * @param nameParser    the parser of person names.
     * @param logger        the logger to use for put a message in the log.
     */
    @SuppressWarnings("static-method")
    protected Person extractPerson(String personDesc, boolean createPerson,
                                   PersonService personService, PersonNameParser nameParser, Logger logger) {
        final var idPattern = Pattern.compile("\\d+"); //$NON-NLS-1$
        return extractPerson(personDesc, idPattern, createPerson, personService, nameParser, logger);
    }

    /**
     * Upload a file.
     *
     * @param explicitRemove indicates if the old path to the uploaded file should be removed from the database.
     * @param uploadedFile   the uploaded file, or {@code null} if no file is uploaded.
     * @param logMessage     a message that is used for output an INFO message about the file upload.
     * @param logger         the logger to use for put a message in the log.
     * @param setter         a callback function that is invoked to change the attribute "pathTo..." in the associated JPA entity.
     *                       The argument of the callback is the path to save.
     * @param filename       a callback function that provides the filename to be saved in the database.
     * @param delete         a callback that is invoked when the path should be removed from the local file system.
     * @param save           a callback invoked to save the file on the file system.
     * @return {@code true} if a file was uploaded or removed.
     * @throws IOException if the file cannot be uploaded.
     * @since 3.2
     */
    @SuppressWarnings("static-method")
    protected boolean updateUploadedFile(boolean explicitRemove, MultipartFile uploadedFile,
                                         String logMessage, Logger logger, Consumer<String> setter, Supplier<File> filename,
                                         Callback delete, Saver save) throws IOException {
        // Treat the uploaded files
        var changed = false;
        if (explicitRemove) {
            try {
                delete.apply();
            } catch (Throwable ex) {
                // Silent
            }
            setter.accept(null);
            changed = true;
        }
        if (uploadedFile != null && !uploadedFile.isEmpty()) {
            final var fn = filename.get();
            final var th = FileSystem.replaceExtension(fn, FileManager.JPEG_FILE_EXTENSION);
            save.apply(fn, th);
            setter.accept(fn.getPath());
            changed = true;
            logger.info(logMessage + fn.getPath());
        }
        return changed;
    }

    /**
     * Build the URL for accessing an endpoint with the given parameter name, but without setting the parameter value.
     *
     * @param endpointName  the name of the endpoint.
     * @param parameterName the parameter name.
     * @return the endpoint URL.
     */
    protected String endpoint(String endpointName, String parameterName) {
        return endpoint(endpointName, parameterName, ""); //$NON-NLS-1$
    }

    /**
     * Build the URL for accessing an endpoint.
     *
     * @param endpointName the name of the endpoint.
     * @return the endpoint URL.
     */
    protected String endpoint(String endpointName) {
        return endpoint(endpointName, null, null, null, null);
    }

    /**
     * Build the URL for accessing an endpoint with the given parameter name, but without setting the parameter value.
     *
     * @param endpointName   the name of the endpoint.
     * @param parameterName  the parameter name.
     * @param parameterValue the parameter value.
     * @return the endpoint URL.
     */
    protected String endpoint(String endpointName, String parameterName, Object value) {
        return endpoint(endpointName, parameterName, value, null, null);
    }

    /**
     * Build the URL for accessing an endpoint with the given parameter names, but without setting the parameter value.
     *
     * @param endpointName    the name of the endpoint.
     * @param parameterName0  the first parameter name.
     * @param parameterValue0 the first parameter value.
     * @param parameterName1  the second parameter name.
     * @return the endpoint URL.
     */
    protected String endpoint(String endpointName, String parameterName0, Object parameterValue0, String parameterName1) {
        return endpoint(endpointName, parameterName0, parameterValue0, parameterName1, ""); //$NON-NLS-1$
    }

    /**
     * Build the URL for accessing an endpoint with the given parameter names.
     *
     * @param endpointName    the name of the endpoint.
     * @param parameterName0  the first parameter name.
     * @param parameterValue0 the first parameter value.
     * @param parameterName1  the second parameter name.
     * @param parameterValue1 the second parameter value.
     * @return the endpoint URL.
     * @sine 3.6
     */
    protected String endpoint(String endpointName, String parameterName0, Object parameterValue0, String parameterName1, String parameterValue1) {
        final UriBuilder b = endpointUriBuilder(endpointName);
        if (!Strings.isNullOrEmpty(parameterName0)) {
            b.queryParam(parameterName0, parameterValue0);
        }
        if (!Strings.isNullOrEmpty(parameterName1)) {
            b.queryParam(parameterName1, parameterValue1);
        }
        return b.build().toASCIIString();
    }

    /**
     * Create the URL builder for accessing an endpoint.
     *
     * @param endpointName the name of the endpoint.
     * @return the endpoint URL. builder
     */
    protected UriBuilder endpointUriBuilder(String endpointName) {
        final var b = this.uriBuilderFactory.builder();
        b.path("/" + this.constants.getServerName() + "/" + endpointName); //$NON-NLS-1$ //$NON-NLS-2$
        return b;
    }

    /**
     * Build the URL from the root of the JPA server.
     *
     * @param relativeFile the relative path to append to the server's root.
     * @return the rooted URL.
     */
    protected String rooted(File relativeFile) {
        final var bb = new StringBuilder();
        File f = relativeFile;
        while (f != null) {
            bb.insert(0, f.getName()).insert(0, "/"); //$NON-NLS-1$
            f = f.getParentFile();
        }
        final var b = this.uriBuilderFactory.builder();
        b.path("/" + this.constants.getServerName() + bb); //$NON-NLS-1$
        return b.build().toASCIIString();
    }

    /**
     * Build the URL from the root of the JPA server.
     *
     * @param relativeUrl the relative URL to append to the server's root.
     * @return the rooted URL.
     */
    protected String rooted(String relativeUrl) {
        final var b = this.uriBuilderFactory.builder();
        b.path("/" + this.constants.getServerName() + "/" + relativeUrl); //$NON-NLS-1$ //$NON-NLS-2$
        return b.build().toASCIIString();
    }

    /**
     * Create a root-based filename for a thumbnail.
     *
     * @param filename              the filename of the picture.
     * @param preserveFileExtension indicates if the filename extension is preserved ({@code true}) or forced to JPEG
     *                              ({@code false}.
     * @return the rooted filename of the picture.
     * @since 3.2
     */
    protected String rootedThumbnail(String filename, boolean preserveFileExtension) {
        try {
            final var url = new URI("file:" + filename).toURL(); //$NON-NLS-1$
            final URL normalized;
            if (preserveFileExtension) {
                normalized = url;
            } else {
                normalized = FileSystem.replaceExtension(url, FileManager.JPEG_FILE_EXTENSION);
            }
            return rooted(normalized.getPath());
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Internal callback object.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 3.0
     */
    @FunctionalInterface
    protected interface Callback {

        /**
         * Callback function.
         *
         * @throws IOException in case of error
         */
        void apply() throws IOException;

    }

    /**
     * Internal callback object.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 3.0
     */
    @FunctionalInterface
    protected interface Saver {

        /**
         * Callback function.
         *
         * @param filename  the name of the file.
         * @param thumbnail the filename of the thumbnail.
         * @throws IOException in case of error
         */
        void apply(File filename, File thumbnail) throws IOException;

    }

}
