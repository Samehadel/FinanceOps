CREATE TABLE chat_message (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role varchar(10) NOT NULL CHECK (role IN ('user', 'assistant')),
    content text NOT NULL
);
CREATE INDEX chat_message_user_idx ON chat_message(user_id, id);
