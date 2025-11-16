package algo.demo.parsers;

import algo.demo.dto.*;
import algo.demo.exceptions.ParsingException;
import algo.demo.validators.CollectionValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FileParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private CollectionValidator collectionValidator;

    public List<LabWork> parseLabWorksFromFile(InputStream fileInputStream)  {
        this.objectMapper.registerModule(new JavaTimeModule());
        List<LabWork> labWorks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"))) {
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            int braceCount = 0;
            boolean inObject = false;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()) {
                    continue;
                }

                for (char c : trimmedLine.toCharArray()) {
                    if (c == '{') {
                        if (!inObject) {
                            inObject = true;
                            jsonBuffer.setLength(0);
                        }
                        braceCount++;
                        jsonBuffer.append(c);
                    } else if (c == '}') {
                        braceCount--;
                        jsonBuffer.append(c);

                        if (inObject && braceCount == 0) {
                            String jsonString = jsonBuffer.toString();
                            try {
                                LabWork labWork = objectMapper.readValue(jsonString, LabWork.class);
                                collectionValidator.validateLabWork(labWork);
                                labWorks.add(labWork);
                            } catch (ParsingException e) {
                                throw new ParsingException("Ошибка в строке " + lineNumber);
                            } catch (Exception e) {
                                throw new ParsingException("Ошибка парсинга JSON в строке " + lineNumber);
                            }
                            inObject = false;
                            jsonBuffer.setLength(0);
                        }
                    } else if (inObject) {
                        jsonBuffer.append(c);
                    }
                }

                if (inObject && braceCount > 0) {
                    jsonBuffer.append("\n");
                }
            }

            if (inObject && braceCount > 0) {
                throw new ParsingException("Незакрытый JSON объект в конце файла");
            }
        } catch (IOException e) {
            throw new ParsingException("Ошибка чтения файла: " + e.getMessage());
        }
        return labWorks;
    }

}
