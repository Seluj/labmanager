/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * Copyright (c) 2019 Kaspar Scherrer
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

package fr.utbm.ciad.labmanager.views.components.addons.progress;

import com.google.common.base.Strings;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionListener;

import java.io.Serializable;
import java.util.function.Function;

/**
 * A progression indicator with prefix message.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class PatternCommentProgression implements Progression, Serializable {

    private static final long serialVersionUID = 2155201432901901389L;

    private final Progression progress;

    private final Function<String, String> formatter;

    /**
     * Constructor.
     *
     * @param progress the progression to delegate to.
     * @param pattern  the comment formatter. The argument is the internal comment part.
     */
    protected PatternCommentProgression(Progression progress, Function<String, String> pattern) {
        this.progress = progress;
        this.formatter = pattern;
    }

    /**
     * Build the extended comment from the pattern.
     *
     * @param comment the comment's internal part.
     * @return the extended comment.
     */
    protected String buildComment(String comment) {
        return this.formatter.apply(Strings.nullToEmpty(comment));
    }

    @Override
    public void addProgressionListener(ProgressionListener listener) {
        this.progress.addProgressionListener(listener);
    }

    @Override
    public void removeProgressionListener(ProgressionListener listener) {
        this.progress.removeProgressionListener(listener);
    }

    @Override
    public int getMinimum() {
        return this.progress.getMinimum();
    }

    @Override
    public void setMinimum(int newMinimum) {
        this.progress.setMinimum(newMinimum);
    }

    @Override
    public int getMaximum() {
        return this.progress.getMaximum();
    }

    @Override
    public void setMaximum(int newMaximum) {
        this.progress.setMaximum(newMaximum);
    }

    @Override
    public int getValue() {
        return this.progress.getValue();
    }

    @Override
    public void setValue(int newValue) {
        this.progress.setValue(newValue);
    }

    @Override
    public double getPercent() {
        return this.progress.getPercent();
    }

    @Override
    public double getProgressionFactor() {
        return this.progress.getProgressionFactor();
    }

    @Override
    public void setValue(int newValue, String comment) {
        this.progress.setValue(newValue, buildComment(comment));
    }

    @Override
    public boolean isAdjusting() {
        return this.progress.isAdjusting();
    }

    @Override
    public void setAdjusting(boolean adjusting) {
        this.progress.setAdjusting(adjusting);
    }

    @Override
    public void setProperties(int value, int min, int max, boolean adjusting, String comment) {
        this.progress.setProperties(value, min, max, adjusting, buildComment(comment));
    }

    @Override
    public void setProperties(int value, int min, int max, boolean adjusting) {
        this.progress.setProperties(value, min, max, adjusting);
    }

    @Override
    public boolean isIndeterminate() {
        return this.progress.isIndeterminate();
    }

    @Override
    public void setIndeterminate(boolean newValue) {
        this.progress.setIndeterminate(newValue);
    }

    @Override
    public String getComment() {
        return this.progress.getComment();
    }

    @Override
    public void setComment(String comment) {
        this.progress.setComment(buildComment(comment));
    }

    @Override
    public Progression subTask(int extent, int min, int max) {
        return this.progress.subTask(extent, min, max);
    }

    @Override
    public Progression subTask(int extent) {
        return this.progress.subTask(extent);
    }

    @Override
    public Progression subTask(int extent, int min, int max, boolean overwriteComment) {
        return this.progress.subTask(extent, min, max, overwriteComment);
    }

    @Override
    public Progression subTask(int extent, boolean overwriteComment) {
        return this.progress.subTask(extent, overwriteComment);
    }

    @Override
    public Progression getSubTask() {
        return this.progress.getSubTask();
    }

    @Override
    public void ensureNoSubTask() {
        this.progress.ensureNoSubTask();
    }

    @Override
    public Progression getSuperTask() {
        return this.progress.getSuperTask();
    }

    @Override
    public void end() {
        this.progress.end();
    }

    @Override
    public boolean isRootModel() {
        return this.progress.isRootModel();
    }

    @Override
    public int getTaskDepth() {
        return this.progress.getTaskDepth();
    }

    @Override
    public void increment(int amount) {
        this.progress.increment(amount);
    }

    @Override
    public void increment(int amount, String comment) {
        this.progress.increment(amount, buildComment(comment));
    }

    @Override
    public void increment() {
        this.progress.increment();
    }

    @Override
    public void increment(String comment) {
        this.progress.increment(buildComment(comment));
    }

}
