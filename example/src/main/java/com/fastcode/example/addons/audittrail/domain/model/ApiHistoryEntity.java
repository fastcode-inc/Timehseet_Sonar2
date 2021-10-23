package com.fastcode.example.addons.audittrail.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "apihistory")
public class ApiHistoryEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6336351395990324861L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation", nullable = true, length = 255)
    @Length(max = 255, message = "correlation must be less than 255 characters")
    private String correlation;

    @Column(name = "path", nullable = true, length = 255)
    @Length(max = 255, message = "path must be less than 255 characters")
    private String path;

    @Column(name = "process_time")
    private long processTime;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(name = "response_time")
    private LocalDateTime responseTime;

    @Column(name = "response_status", length = 5)
    private String responseStatus;

    @Column(name = "user_name", nullable = true, length = 32)
    @Length(max = 32, message = "Username must be less than 32 characters")
    private String userName;

    @Column(name = "method", nullable = true, length = 10)
    @Length(max = 10, message = "method must be less than 10 characters")
    private String method;

    @Column(name = "content_type", nullable = true, length = 100)
    @Length(max = 100, message = "ContentType must be less than 100 characters")
    private String contentType;

    @Column(name = "query", nullable = true, length = 1000)
    @Length(max = 1000, message = "query must be less than 1000 characters")
    private String query;

    @Column(name = "client_address", nullable = true, length = 100)
    @Length(max = 100, message = "clientAddress must be less than 100 characters")
    private String clientAddress;

    @Column(name = "scheme", nullable = true, length = 100)
    @Length(max = 100, message = "scheme must be less than 100 characters")
    private String scheme;

    @Column(name = "header", nullable = true, length = 2000)
    @Length(max = 2000, message = "Header must be less than 2000 characters")
    private String header;

    @Column(name = "body", nullable = true, length = 4000)
    @Length(max = 4000, message = "Body must be less than 4000 characters")
    private String body;

    @Column(name = "browser", length = 1000)
    private String browser;

    @Column(name = "response", length = 1000)
    private String response;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiHistoryEntity)) return false;
        ApiHistoryEntity obj = (ApiHistoryEntity) o;
        return id != null && id.equals(obj.id);
    }

    @Override
    public int hashCode() {
        return 131;
    }
}
