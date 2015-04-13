package mixter.infra.repositories;

import mixter.domain.core.message.MessageId;
import mixter.domain.core.message.TimelineMessage;
import mixter.domain.identity.UserId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryTimelineRepositoryTest {

    public static final UserId USER_ID = new UserId("mail@mix-it.fr");
    public static final UserId AUTHOR_ID = new UserId("authro@mix-it.fr");
    public static final String CONTENT = "content";
    private static final UserId OTHER_USER_ID = new UserId("other@mix-it.fr");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void emptyRepositoryShouldReturnNoMessages() {
        // Given
        InMemoryTimelineRepository repository = new InMemoryTimelineRepository();
        thrown.expect(NoSuchElementException.class);
        // When
        TimelineMessage byMessageId = repository.getByMessageId(new MessageId());
    }

    @Test
    public void shouldRetrieveSavedMessageById() {
        // Given
        InMemoryTimelineRepository repository = new InMemoryTimelineRepository();
        TimelineMessage message = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, new MessageId());

        // When
        repository.save(message);

        assertThat(repository.getByMessageId(message.getMessageId())).isEqualTo(message);
    }
    @Test
    public void shouldRetrieveSavedMessageByIdWhenMultipleMessagesSaved() {
        // Given
        InMemoryTimelineRepository repository = new InMemoryTimelineRepository();
        TimelineMessage message = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, new MessageId());
        TimelineMessage message2 = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, new MessageId());

        repository.save(message);
        repository.save(message2);

        // When
        assertThat(repository.getByMessageId(message.getMessageId())).isEqualTo(message);
    }
    @Test
    public void shouldRetrieveOneMessageByIdWhenMessageSavedMultipleTimes() {
        // Given
        InMemoryTimelineRepository repository = new InMemoryTimelineRepository();
        MessageId messageId = new MessageId();
        TimelineMessage message = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, messageId);
        TimelineMessage message2 = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, messageId);

        repository.save(message);
        repository.save(message2);

        // When
        assertThat(repository.getByMessageId(message.getMessageId())).isEqualTo(message);
    }

    @Test
    public void shouldRetrieveSavedMessageByUserId() throws Exception {
        // Given
        InMemoryTimelineRepository repository = new InMemoryTimelineRepository();
        MessageId messageId = new MessageId();
        TimelineMessage message = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, messageId);

        repository.save(message);

        // When
        assertThat(repository.getByUserId(USER_ID)).containsExactly(message);
    }

    @Test
    public void shouldRetrieveSavedMessageByUserIdOnlyForRequestedUserId() throws Exception {
        // Given
        InMemoryTimelineRepository repository = new InMemoryTimelineRepository();
        TimelineMessage message = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, new MessageId());
        TimelineMessage message3 = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, new MessageId());
        TimelineMessage message4 = new TimelineMessage(USER_ID, AUTHOR_ID, CONTENT, new MessageId());
        TimelineMessage message2 = new TimelineMessage(OTHER_USER_ID, AUTHOR_ID, CONTENT, new MessageId());

        repository.save(message);
        repository.save(message2);
        repository.save(message3);
        repository.save(message4);

        // When
        assertThat(repository.getByUserId(USER_ID)).containsExactly(message, message3,message4);
    }
}