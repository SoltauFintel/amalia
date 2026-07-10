//new UsernamePasswordCredentialsProvider(repo.getUser(), repo.getPassword())
package github.soltaufintel.amalia.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.base.FileService;

/**
 * Git repository
 */
public class Repository {
    private static final Object LOCK = new Object();
    private final RepositoryDefinition repo;
    private Git git;
    private String currentBranch = "master";
    private boolean onlyRemoteBranches = true;

    public Repository(RepositoryDefinition repo) {
        this.repo = repo;
    }

    // 2. Die neue Methode, um den Branch vorab zu setzen
    public void switchToBranch(String branchName) {
        if (branchName != null && !branchName.isBlank()) {
            this.currentBranch = branchName;
        }
    }

    public void fetch(boolean bare) {
        fetchOrPull(false, bare);
    }

    public void pull(boolean bare) {
        fetchOrPull(true, bare);
    }
    
    private void fetchOrPull(boolean pull, boolean bare) {
        if (repo.getLocalFolder().isDirectory()) {
            synchronized (LOCK) {
                try {
                    var git = getGit();
                    
                    // Sicherstellen, dass das lokale Repo auf dem richtigen Branch steht und diesen trackt
                    checkoutAndTrackBranch(git);

                    var cmd = pull ? git.pull() : git.fetch();
                    cmd.setCredentialsProvider(cred());
                    cmd.call();
                } catch (Exception e) {
                    Logger.error((pull ? "pull" : "fetch") + " error: " + repo.getUrl() + " => " + repo.getLocalFolder().getAbsolutePath());
                    Logger.error(e);
                    throw new RuntimeException("Error " + (pull ? "pulling" : "fetching") + " Git repository", e);
                }
            }
        } else {
            cloneRepo(bare);
        }
    }

    // Hilfsmethode, um den lokalen Branch zu wechseln und ggf. Remote-Tracking einzurichten
    private void checkoutAndTrackBranch(Git git) throws GitAPIException, IOException {
        String localBranch = git.getRepository().getBranch();
        if (!currentBranch.equals(localBranch)) {
            Logger.info("Switching branch from " + localBranch + " to " + currentBranch);
            
            // Prüfen, ob der lokale Branch bereits existiert
            boolean localExists = git.branchList().call().stream()
                    .anyMatch(ref -> org.eclipse.jgit.lib.Repository.shortenRefName(ref.getName()).equals(currentBranch));

            var checkoutCmd = git.checkout().setName(currentBranch);
            
            if (!localExists) {
                // Wenn er lokal nicht existiert, erstellen wir ihn und binden ihn an den Remote-Branch
                checkoutCmd.setCreateBranch(true)
                           .setStartPoint("origin/" + currentBranch);
            }
            
            checkoutCmd.call();
        }
    }

    public void cloneRepo(boolean bare) {
        close();
        synchronized (LOCK) {
            try {
                if (repo.getLocalFolder().isDirectory()) {
                    FileUtils.delete(repo.getLocalFolder(), FileUtils.RECURSIVE);
                }
                Files.createDirectory(repo.getLocalFolder().toPath());
    
                Logger.info("cloning Git repository (" + currentBranch + ") " + repo.getUrl() + " => " + repo.getLocalFolder().getAbsolutePath());
                CloneCommand clone = Git.cloneRepository();
                clone.setDirectory(repo.getLocalFolder());
                clone.setURI(repo.getUrl());
                clone.setCredentialsProvider(cred());
                clone.setBare(bare);
                
                // 3. Dem Clone-Befehl sagen, welchen Branch er auschecken soll
                clone.setBranch(currentBranch); 
                
                clone.call();
                Logger.info("  clone ok");
            } catch (GitAPIException | IOException e) {
                Logger.error("clone error: " + repo.getUrl() + " => " + repo.getLocalFolder().getAbsolutePath());
                Logger.error(e);
                throw new RuntimeException("Error cloning Git repository", e);
            }
        }
    }

    private Git getGit() {
        if (git == null) {
            try {
                git = Git.open(repo.getLocalFolder());
            } catch (IOException e) {
                Logger.error("open Git error: " + repo.getUrl() + " => " + repo.getLocalFolder().getAbsolutePath());
                Logger.error(e);
                throw new RuntimeException("Error opening Git repository");
            }
        }
        return git;
    }
    
    public Commit loadCommit(String commitId) {
        RevCommit commit = loadRevCommit(commitId);
        if (commit == null) {
            return null;
        }
        return new CommitBuilder().build(commit, getChanges2(commit));
    }
    
    private RevCommit loadRevCommit(String id) { // teuer
        ObjectId commitId = ObjectId.fromString(id);
        try (RevWalk revWalk = new RevWalk(getGit().getRepository())) {
            return revWalk.parseCommit(commitId);
        } catch (Exception e) {
            Logger.error("Error loading commit #" + id + ": " + e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * @param contains null: no filter
     * @return tag list
     */
    public List<Tag> getTags(String contains) {
        synchronized (LOCK) {
            try {
                Git git = getGit();
                var c = git.tagList().call().stream();
                if (contains != null) {
                    c = c.filter(tag -> tag.getName().contains(contains));
                }
                return c.map(tag -> new Tag(tag, git))
                        .collect(Collectors.toList());
            } catch (GitAPIException e) {
                throw new RuntimeException("Error loading tags", e);
            }
        }
    }
    
    /**
     * @return list of tag names
     */
    public List<String> getTagNames() {
        return getTags(null).stream().map(Tag::getName).collect(Collectors.toList());
    } 
    
    /**
     * Creates tag on given commit and pushs it.
     * @param tagName e.g. "3.18.4"
     * @param commit null: current commit, otherwise commit hash or tag name
     * @param user user to log into remote Git repository, e.g. "builder", null: don't push
     * @param password password to log into remote Git repository
     * @param msg (new argument) "tagged by git-service-" + VERSION + ".jar"
     */
    public void tag(String tagName, String commit, String user, String password, String msg) {
        // TODO Maybe there's an better implementation for this!
        String m = null;
        if (commit != null) {
            m = getBranch();
            selectCommit(commit);
        }
        try {
            doTag(tagName, user, password, msg);
        } finally {
            if (commit != null) {
                switchToBranch(m);
            }
        }
    }
    
    private void doTag(String tagName, String user, String password, String msg) {
        String action = "creating";
        try {
            var git = getGit();
            // step 1: create
            git.tag()
                .setName(tagName)
                .setMessage(msg)
                .call();
            
            // step 2: push
            if (user != null) {
                try {
                    action = "pushing";
                    git.push()
                        .setPushTags()
                        .setRefSpecs(new RefSpec(tagName))
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, password))
                        .call();
                } catch (Exception up) {
                    try {
                        deleteTag(tagName, git);
                        Logger.warn("tag push error! -> compensation: local tag " + tagName + " deleted");
                    } catch (Throwable ignore) { //
                    }
                    throw up;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error " + action + " a tag! " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete local tag
     * @param tagName e.g. "3.12.4"
     */
    public void deleteTag(String tagName) {
        try {
            deleteTag(tagName, getGit());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting local tag " + tagName + "\n" + e.getMessage(), e);
        }
    }
    
    /**
     * Delete all tags that a returned by getTags(). Does no push.
     */
    public void clearTags() {
        try {
            var git = getGit();
            for (Tag tag : getTags(null)) {
                deleteTag(tag.getName(), git);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting all local tags!\n" + e.getMessage(), e);
        }
    }
    
    private void deleteTag(String tagName, Git git) throws GitAPIException {
        git.tagDelete()
            .setTags(tagName)
            .call();
    }
    /**
     * Creates branch on current commit.
     * This method creates no root tag!
     * See other branch() method if you also want to push the commit.
     * @param name branch name, e.g. "3.21.x"
     */
    public void branch(String name) {
        branch(name, null, null, null);
    }

    /**
     * Creates branch and pushs it.
     * This method creates no root tag!
     * @param name branch name, e.g. "3.21.x"
     * @param commit null: current commit, otherwise commit hash or tag name
     * @param user user to log into remote Git repository, e.g. "builder", null: don't push
     * @param password password to log into remote Git repository
     */
    public void branch(String name, String commit, String user, String password) {
        // TODO Maybe there's an better implementation for this! branchCreate().setStartPoint?
        String m = null;
        if (commit != null) {
            m = getBranch();
            selectCommit(commit);
        }
        try {
            branch(name, user, password);
        } finally {
            if (commit != null) {
                switchToBranch(m);
            }
        }
    }
    
    private void branch(String name, String user, String password) {
        String action = "creating";
        try {
            var git = getGit();
            // step 1: create
            git.branchCreate()
                .setName(name)
                .call();
            
            // step 2: push
            if (user != null) {
                try {
                    action = "pushing";
                    git.push()
                        .setRemote("origin")
                        .setRefSpecs(new RefSpec(name + ":" + name))
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, password))
                        .call();
                } catch (Exception up) {
                    try {
                        git.branchDelete()
                            .setBranchNames(name)
                            .call();
                        Logger.warn("branch push error! -> compensation: local branch " + name + " deleted");
                    } catch (Throwable ignore) { //
                    }
                    throw up;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error " + action + " a branch! " + e.getMessage(), e);
        }
    }
    
    /**
     * checkout: Set HEAD to other commit
     * @param commit commit hash, can be short form
     * A tag name should also work.
     */
    public void selectCommit(String commit) {
        try {
            getGit().checkout()
                .setName(commit)
                .call();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error selecting commit! " + e.getMessage(), e);
        }
    }
    
    /**
     * onlyRemoteBranches setting is used.
     * @return branch list
     */
    public List<Branch> getBranches() {
        try {
            var git = getGit();
            return git.branchList()
                    .setListMode(onlyRemoteBranches ? ListMode.REMOTE : ListMode.ALL)
                    .call()
                    .stream()
                    .filter(ref -> !"HEAD".equals(ref.getName()))
                    .map(ref -> new Branch(ref, git))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error loading branches!", e);
        }
    }
    
    /**
     * onlyRemoteBranches setting is used.
     * @return branch names as list
     */
    public List<String> getBranchNames() {
        return getBranches().stream()
                .map(Branch::getName)
                .collect(Collectors.toList());
    }
    
    public String getBranchStartDate(String branch) {
        // Hier dynamisch gegen den gesetzten Branch prüfen statt hartcodiert gegen "master"
        if (!currentBranch.equals(branch)) {
            synchronized (LOCK) {
                String x = "root_" + branch;
                try {
                    Git git = getGit();
                    List<Ref> tags = git.tagList().call();
                    for (Ref ref : tags) {
                        String name = org.eclipse.jgit.lib.Repository.shortenRefName(ref.getName());
                        if (name.equals(x)) {
                            try (RevWalk walk = new RevWalk(git.getRepository())) {
                                RevCommit c = walk.parseCommit(ref.getObjectId());
                                if (c != null) {
                                    return new CommitBuilder().getCommitDate(c);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.error(e);
                }
            }
        }
        return "";
    }

    public String getChanges(String commitId) {
        RevCommit commit = loadRevCommit(commitId);
        if (commit == null) {
            return commitId;
        }
        return getChanges2(commit);
    }

    private String getChanges2(RevCommit commit) { // teuer
        synchronized (LOCK) {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            try {
                if (commit.getParentCount() == 0) {
                    return "first commit #" + commit.getId().getName();
                }
                RevCommit parent = commit.getParent(0);
                try (DiffFormatter diffFormatter = new DiffFormatter(boas)) {
                    diffFormatter.setRepository(getGit().getRepository());
                    for (DiffEntry entry : diffFormatter.scan(parent, commit)) {
                        diffFormatter.format(diffFormatter.toFileHeader(entry));
                    }
                }
                return new String(boas.toByteArray());
            } catch (Throwable e) {
                if (e instanceof OutOfMemoryError || e.getCause() instanceof OutOfMemoryError) {
                    // 38553635 Bytes = 36 MB      XDEV-5823
                    return "No changes because of OutOfMemoryError for commit #" + commit.getId().getName();
                }
                Logger.error("getChanges error for commit "
                        + (commit == null ? "<commit is null>" : commit.getId().getName()));
                Logger.error(e);
                return "No changes because of an " + e.getClass().getName() + " for commit #" + (commit == null ? "<commit is null>" : commit.getId().getName());
            }
        }
    }
    
    /**
     * @return hash of current commit (HEAD), e.g. "f65bb8c600a3ea1eabdbdcad1f6bd381f00636b6"
     */
    public String getCurrentCommitHash() {
        synchronized (LOCK) {
            try {
                Iterator<RevCommit> iter = getGit().log().setMaxCount(1).call().iterator();
                var ret = iter.hasNext() ? iter.next().getName() : "-";
                Logger.debug("Repository.getCurrentCommitHash: " + ret);
                return ret;
            } catch (Exception e) {
                Logger.error(e);
                return "?";
            }
        }
    }
    
    public boolean commitAndPushFile_ifChanged(String filename, String content, String authorName, String mail, String commitMessage) {
        boolean ret = false;
        synchronized (LOCK) {
            pull(false);
            var file = new File(repo.getLocalFolder(), filename);
            String oldContent = FileService.loadPlainTextFile(file);
            if (oldContent == null || !oldContent.equals(content)) {
                FileService.savePlainTextFile(file, content);
                commitAndPush(filename, authorName, mail, commitMessage);
                ret = true;
            }
            close();
            return ret;
        }
    }
    
    public void commitAndPush(String filename, String authorName, String mail, String commitMessage) {
        synchronized (LOCK) {
            try {
                git.add().addFilepattern(filename).call();
                git.commit() //
                    .setAuthor(authorName, mail) //
                    .setCommitter(authorName, mail) //
                    .setMessage(commitMessage) //
                    .call();
                // Push betrifft nun automatisch den aktuell ausgecheckten Branch
                git.push().setRemote("origin").setCredentialsProvider(cred()).call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * Add all changes and commit them.
     * See other commit() method if you also want to push the commit.
     * @param commitMessage -
     * @param authorName also committer name
     * @param mail email address of author/committer
     * @return commit hash of newly created commit
     */
    public String commit(String commitMessage, String authorName, String mail) {
        return commit(commitMessage, authorName, mail, null, null);
    }
    
    /**
     * Add all changes, commit and push them.
     * @param commitMessage -
     * @param authorName also committer name
     * @param mail email address of author/committer
     * @param user user to log into remote Git repository, null: don't push
     * @param password password to log into remote Git repository
     * @return commit hash of newly created commit
     */
    public String commit(String commitMessage, String authorName, String mail, String user, String password) {
        return commit(commitMessage, authorName, mail, user, password, "."/*=all files*/);
    }
    
    public String commit(String commitMessage, String authorName, String mail, String user, String password, String filepattern) {
        if (commitMessage == null || commitMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("commitMessage must not be empty!");
        }
        if (authorName == null || authorName.trim().isEmpty()) {
            throw new IllegalArgumentException("authorName must not be empty!");
        }
        if (mail == null || mail.trim().isEmpty()) {
            throw new IllegalArgumentException("mail must not be empty!");
        }
        try {
            var git = getGit();
            git.add()
                .addFilepattern(filepattern)
                .call();
            RevCommit commit = git.commit()
                .setMessage(commitMessage)
                .setAuthor(authorName, mail)
                .setCommitter(authorName, mail)
                .call();
            if (user != null) {
                git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, password))
                    .call();
            }
            return commit.getName();
        } catch (Exception e) {
            throw new RuntimeException("Error committing changes!\n" + e.getMessage(), e);
        }
    }

    private UsernamePasswordCredentialsProvider cred() {
        Credentials cred = getCredentials();
        return new UsernamePasswordCredentialsProvider(cred.getUser(), cred.getPassword());
    }
    
    protected Credentials getCredentials() {
        return repo;
    }
    
    public void close() {
        if (git != null) {
            try {
                git.close();
            } catch (Exception ignore) {
            }
            git = null;
        }
    }

    public GitFileChanges getFileChanges(String commitId) {
        return loadFileChanges(loadRevCommit(commitId), getGit().getRepository());
    }
    
    public static GitFileChanges loadFileChanges(RevCommit commit, org.eclipse.jgit.lib.Repository repository) {
        if (commit == null || commit.getParentCount() != 1) {
            return null;
        }
        Commit bc = new CommitBuilder().build(commit, null);
        List<GitFileChange> changes = new ArrayList<>();
        RevCommit parent = commit.getParent(0);
        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repository);

            List<DiffEntry> diffEntries = diffFormatter.scan(parent, commit);
            for (DiffEntry entry : diffEntries) {
                String path = entry.getChangeType() == ChangeType.DELETE ? entry.getOldPath() : entry.getNewPath();
                changes.add(new GitFileChange(path, entry.getChangeType().name()));
            }
        } catch (IOException e) {
            Logger.error("Error while getting file changes for commit " + commit.getId().getName(), e);
        }
        return new GitFileChanges(bc, changes);
    }
    
    public String getBranch() {
        try {
            return git.getRepository().getBranch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * This call may take 4 seconds.
     * @return false if there is a change in the work tree, e.g. an added or changed file
     * <br>true if there is nothing to be committed (but maybe something to push)
     * <br>Of course any changes in gitignore folders are not detected (e.g. build folder).
     */
    public boolean isWorkspaceClean() {
        try {
            return getGit().status().call().isClean();
        } catch (Exception e) {
            throw new RuntimeException("Error while detecting if workspace is clean!", e);
        }
    }
    
    public void resetHard() {
        try {
            getGit().reset().setMode(ResetType.HARD).call();
        } catch (Exception e) {
            throw new RuntimeException("Error while executing 'reset hard'!", e);
        }
    }

    /**
     * @return true: only remote branch (default), false: all branches
     */
    public boolean isOnlyRemoteBranches() {
        return onlyRemoteBranches;
    }

    public void setOnlyRemoteBranches(boolean onlyRemoteBranches) {
        this.onlyRemoteBranches = onlyRemoteBranches;
    }
}
