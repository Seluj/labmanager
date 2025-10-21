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

package fr.utbm.ciad.labmanager.data.supervision;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Description of a supervisor.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.1
 */
@Entity
@Table(name = "Supervisors")
public class Supervisor implements Serializable, AttributeProvider, Comparable<Supervisor>, IdentifiableEntity {

    private static final long serialVersionUID = -2851789340464408145L;

    /**
     * Identifier of the jury.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Promoter or director of the candidate.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Person supervisor;

    /**
     * Percentage of supervision.
     */
    @Column
    private int percentage;

    /**
     * Type of supervisor.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private SupervisorType type = SupervisorType.SUPERVISOR;

    /**
     * Construct an empty supervisor.
     */
    public Supervisor() {
        //
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.supervisor);
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final var other = (Supervisor) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.supervisor, other.supervisor);
    }

    @Override
    public int compareTo(Supervisor o) {
        return EntityUtils.getPreferredSupervisorComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getType() != null) {
            consumer.accept("type", getType()); //$NON-NLS-1$
        }
        if (getPercentage() > 0 && getPercentage() <= 100) {
            consumer.accept("percentage", Integer.valueOf(getPercentage())); //$NON-NLS-1$
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the membership identifier.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the supervisor.
     *
     * @return the supervisor.
     */
    public Person getSupervisor() {
        return this.supervisor;
    }

    /**
     * Change the supervisor.
     *
     * @param supervisor the supervisor.
     */
    public void setSupervisor(Person supervisor) {
        this.supervisor = supervisor;
    }

    /**
     * Replies the percentage of supervision.
     *
     * @return the percentage of supervision.
     */
    public int getPercentage() {
        return this.percentage;
    }

    /**
     * Change the percentage of supervision.
     *
     * @param percentage the percentage of supervision between 0 and 100.
     */
    public void setPercentage(int percentage) {
        final int p;
        if (percentage < 0) {
            p = 0;
        } else if (percentage > 100) {
            p = 100;
        } else {
            p = percentage;
        }
        this.percentage = p;
    }

    /**
     * Change the percentage of supervision.
     *
     * @param percentage the percentage of supervision between 0 and 100.
     */
    public final void setPercentage(Number percentage) {
        if (percentage == null) {
            setPercentage(0);
        } else {
            setPercentage(percentage.intValue());
        }
    }

    /**
     * Replies the type of supervisor.
     *
     * @return the type.
     */
    public SupervisorType getType() {
        return this.type;
    }

    /**
     * Change the type of supervisor.
     *
     * @param type the type of supervisor.
     */
    public void setType(SupervisorType type) {
        if (type == null) {
            this.type = SupervisorType.SUPERVISOR;
        } else {
            this.type = type;
        }
    }

    /**
     * Change the type of supervisor.
     *
     * @param type the type of supervisor.
     */
    public final void setType(String type) {
        if (Strings.isNullOrEmpty(type)) {
            setType((SupervisorType) null);
        } else {
            setType(SupervisorType.valueOfCaseInsensitive(type));
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this);
    }

}
