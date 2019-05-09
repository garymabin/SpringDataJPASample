package com.thoughtworks.demo.persistence.record;

import com.thoughtworks.demo.domain.aggregates.IEventAggregate;
import com.thoughtworks.demo.persistence.dao.EventDAO;
import com.thoughtworks.demo.util.SpringEntityListener;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "event")
@Data
@Builder
@EntityListeners(value = {SpringEntityListener.class})
public class EventAuditRecord implements IEventAggregate {

    @Tolerate
    EventAuditRecord(){
    }

    @GeneratedValue
    @Id
    private Long id;

    @Column
    private String type;

    @Column(name = "created_at")
    private Instant createdAt;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Transient
    @Autowired
    private EventDAO eventDAO;
}
