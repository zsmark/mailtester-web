package hu.giro.smtpserver.server;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import org.springframework.stereotype.Service;
import tech.blueglacier.email.Email;
import tech.blueglacier.parser.CustomContentHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class EmailParserService {

  public Email parse(byte []emailContent) throws IOException, MimeException {

    CustomContentHandler contentHandler = new CustomContentHandler();
    MimeConfig mime4jParserConfig = MimeConfig.DEFAULT;
    BodyDescriptorBuilder bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();
    MimeStreamParser mime4jParser = new MimeStreamParser(mime4jParserConfig, DecodeMonitor.SILENT, bodyDescriptorBuilder);
    mime4jParser.setContentDecoding(true);
    mime4jParser.setContentHandler(contentHandler);

    InputStream mailIn = new ByteArrayInputStream(emailContent);
    mime4jParser.parse(mailIn);

    return contentHandler.getEmail();
  }
}
