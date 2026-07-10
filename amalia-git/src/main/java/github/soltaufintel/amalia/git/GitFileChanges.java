package github.soltaufintel.amalia.git;

import java.util.List;

public record GitFileChanges(Commit commit, List<GitFileChange> changes) {
}
