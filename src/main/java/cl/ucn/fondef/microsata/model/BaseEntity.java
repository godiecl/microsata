/*
 * Copyright (c) 2022. Fondef IDeA I+D. Universidad Católica del Norte.
 */

package cl.ucn.fondef.microsata.model;

import cl.ucn.fondef.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import java.time.Instant;
import java.util.HashMap;

/**
 * The Base Entity.
 *
 * @author Diego Urrutia-Astorga.
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public abstract class BaseEntity {

    /**
     * The Id.
     */
    @Getter
    @Setter // TODO: Check if we need this?
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The Version.
     */
    @Version
    @Column
    private Long version;

    /**
     * The Created.
     */
    // @JsonUtils.InvisibleJson
    @CreatedDate
    private Instant createdAt;

    /**
     * The Updated.
     */
    // @JsonUtils.InvisibleJson
    @LastModifiedDate
    private Instant modifiedAt;

    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * Return the String representation.
     *
     * @param obj to represent.
     * @return the String.
     */
    public static String toString(Object obj) {
        return JsonUtils.toJson(obj);
        // return ToStringBuilder.reflectionToString(o, RecursiveToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * Just before the call to insert.
     */
    @PrePersist
    private void prePersist() {
        if (this.id != null && this.id == 0L) {
            log.warn("Fixing the zero id for class: {}", this.getClass().getName());
            this.id = null;
        }
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those
     * provided by {@link HashMap}.
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        return this.id != null && this.id.equals(((BaseEntity) obj).getId());
    }
}
