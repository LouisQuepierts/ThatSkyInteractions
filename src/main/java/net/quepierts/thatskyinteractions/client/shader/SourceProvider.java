package net.quepierts.thatskyinteractions.client.shader;

public interface SourceProvider {
    ProgramSource getSource(String file, String directory);
}
