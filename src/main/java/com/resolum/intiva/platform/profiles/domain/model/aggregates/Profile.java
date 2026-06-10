package com.resolum.intiva.platform.profiles.domain.model.aggregates;

import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.ImageURL;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Aggregate root representing a user profile.
 *
 * A profile stores personal information associated with an IAM user.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "profiles")
@NoArgsConstructor
public class Profile extends AuditableAbstractAggregate<Profile> {

    // Default avatar URL for profiles without a custom image
    private static final String DEFAULT_AVATAR_URL = "https://res.cloudinary.com/dcppsmlzd/image/upload/v1781121388/avatar_default_kf0yvc.png";

    // Public ID used to identify the default avatar in Cloudinary
    private static final String DEFAULT_AVATAR_PUBLIC_ID = "avatar_default_kf0yvc";

    /**
     * Display name of the profile owner.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Age of the profile owner.
     */
    @Column(nullable = false)
    private int age;

    /**
     * Avatar image associated with the profile.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "url", column = @Column(name = "avatar_url", nullable = false)),
            @AttributeOverride(name = "publicId", column = @Column(name = "avatar_public_id", nullable = false))
    })
    private ImageURL avatarUrl;

    /**
     * Contact phone number of the profile owner.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Date of birth of the profile owner.
     */
    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    /**
     * Short biography of the profile owner.
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * Reference to the IAM user who owns this profile.
     */
    @Embedded
    private UserId userId;

    /**
     * Creates a new profile linked to a user.
     *
     * @param userId    IAM user identifier (Long)
     * @param name      display name
     * @param age       age
     * @param birthDate date of birth
     * @param avatarUrl optional avatar image URL (uses default if null or blank)
     * @param publicId  optional Cloudinary public ID for the avatar
     */
    @Builder
    public Profile(
            Long userId,
            String name,
            int age,
            LocalDateTime birthDate,
            String avatarUrl,
            String publicId) {
        if (userId == null) throw new IllegalArgumentException("User ID cannot be null");
        validateText(name, "Profile name");

        this.userId = new UserId(userId);
        this.name = name;
        this.age = age;
        this.birthDate = birthDate;
        this.phoneNumber = "";
        this.bio = "";
        applyAvatar(avatarUrl, publicId);
    }

    /**
     * Updates the personal information fields of the profile.
     *
     * @param name        new display name
     * @param bio         new biography
     * @param phoneNumber new phone number
     * @return updated profile
     */
    public Profile updatePersonalInfo(String name, String bio, String phoneNumber, Integer age) {
        if (name != null && !name.isBlank())
            this.name = name;
        if (bio != null)
            this.bio = bio;
        if (phoneNumber != null)
            this.phoneNumber = phoneNumber;
        if (age != null)
            this.age = age;
        return this;
    }

    /**
     * Replaces the profile avatar with a new image.
     *
     * @param avatarUrl new image URL
     * @param publicId  new Cloudinary public ID
     * @return updated profile
     */
    public Profile updateAvatar(String avatarUrl, String publicId) {
        applyAvatar(avatarUrl, publicId);
        return this;
    }

    /**
     * Indicates whether the profile uses the default avatar.
     *
     * @return true if the default avatar is used
     */
    public boolean hasDefaultAvatar() {
        return this.avatarUrl != null && DEFAULT_AVATAR_PUBLIC_ID.equals(this.avatarUrl.publicId());
    }

    private void applyAvatar(String avatarUrl, String publicId) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            this.avatarUrl = new ImageURL(DEFAULT_AVATAR_URL, DEFAULT_AVATAR_PUBLIC_ID);
            return;
        }
        this.avatarUrl = new ImageURL(avatarUrl, publicId);
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or blank");
        }
    }
}