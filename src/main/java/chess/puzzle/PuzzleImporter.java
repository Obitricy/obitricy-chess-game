package chess.puzzle;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class PuzzleImporter {

    private PuzzleImporter() {
    }

    public static PuzzleManager load(String resource) {

        PuzzleManager manager = new PuzzleManager();

        try {

            InputStream in =
                    PuzzleImporter.class.getResourceAsStream(resource);

            if (in == null) {
                throw new IllegalArgumentException(
                        "Puzzle file not found: " + resource
                );
            }

            Reader reader =
                    new InputStreamReader(
                            in,
                            StandardCharsets.UTF_8
                    );

            CSVParser parser =
                    CSVFormat.DEFAULT
                            .builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .build()
                            .parse(reader);

            for (CSVRecord record : parser) {

                manager.addPuzzle(parsePuzzle(record));

            }

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        return manager;
    }

    private static Puzzle parsePuzzle(CSVRecord record) {

        String id = record.get("id");

        String fen = record.get("fen");

        int rating =
                Integer.parseInt(
                        record.get("rating")
                );

        String themes =
                record.get("theme");

        String opening =
                record.get("title");

        List<PuzzleMove> solution =
                new ArrayList<>();

        for (String move :
                record.get("moves").split("\\s+")) {

            solution.add(
                    MoveParser.parse(move)
            );
        }

        return new Puzzle(
                id,
                rating,
                themes,
                opening,
                fen,
                solution
        );
    }

}