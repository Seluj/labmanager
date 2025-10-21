/*
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

package fr.utbm.ciad.labmanager.views.components.addons.entities;

import fr.utbm.ciad.labmanager.data.IdentifiableEntity;

import java.io.Serializable;

/**
 * Container of identifiable entities for a {@link AbstractTwoLevelTreeListView}.
 *
 * @param <R> the type of the root entities, that must be {@link IdentifiableEntity} to be able to provide their id.
 * @param <C> the type of the child entities, that must be {@link IdentifiableEntity} to be able to provide their id.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class TreeListEntity<R extends IdentifiableEntity, C extends IdentifiableEntity>
        implements IdentifiableEntity, Serializable {

    private static final long serialVersionUID = -201369977134196410L;

    private final R root;

    private final C child;

    /**
     * Constructor for a root entity.
     *
     * @param root  the root entity.
     * @param child the child entity. This argument is ignored if a {@code root} entity is provided.
     */
    private TreeListEntity(R root, C child) {
        assert root != null || child != null;
        this.root = root;
        this.child = root == null ? child : null;
    }

    /**
     * Create a root entity.
     *
     * @param <R>    the type of the root entities, that must be {@link IdentifiableEntity} to be able to provide their id.
     * @param <C>    the type of the child entities, that must be {@link IdentifiableEntity} to be able to provide their id.
     * @param entity the root entity.
     * @return the created entity.
     */
    public static <R extends IdentifiableEntity, C extends IdentifiableEntity> TreeListEntity<R, C> root(R entity) {
        return new TreeListEntity<>(entity, null);
    }

    /**
     * Create a child entity.
     *
     * @param <R>    the type of the root entities, that must be {@link IdentifiableEntity} to be able to provide their id.
     * @param <C>    the type of the child entities, that must be {@link IdentifiableEntity} to be able to provide their id.
     * @param entity the child entity.
     * @return the created entity.
     */
    public static <R extends IdentifiableEntity, C extends IdentifiableEntity> TreeListEntity<R, C> child(C entity) {
        return new TreeListEntity<>(null, entity);
    }

    /**
     * Replies if this entity is for a root entity.
     *
     * @return {@code true} if the entity is a root entity.
     */
    public boolean isRootEntity() {
        return this.root != null;
    }

    /**
     * Replies the root entity.
     *
     * @return the entity or {@code null}.
     */
    public R getRootEntity() {
        return this.root;
    }

    /**
     * Replies if this entity is for a child entity.
     *
     * @return {@code true} if the entity is a child entity.
     */
    public boolean isChildEntity() {
        return this.child != null;
    }

    /**
     * Replies the child entity.
     *
     * @return the entity or {@code null}.
     */
    public C getChildEntity() {
        return this.child;
    }

    @Override
    public long getId() {
        if (this.root != null) {
            return this.root.getId();
        }
        if (this.child != null) {
            return this.child.getId();
        }
        return 0;
    }

    @Override
    public int hashCode() {
        if (this.root != null) {
            return this.root.hashCode();
        }
        if (this.child != null) {
            return this.child.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this.root != null) {
            return this.root.equals(obj);
        }
        if (this.child != null) {
            return this.child.equals(obj);
        }
        return super.equals(obj);
    }

}
