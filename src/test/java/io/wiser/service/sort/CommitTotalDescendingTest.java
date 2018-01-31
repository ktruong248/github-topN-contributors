package io.wiser.service.sort;

import io.wiser.service.ContributorStats;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class CommitTotalDescendingTest {

    private CommitTotalDescending sortDescending;

    @Before
    public void setup() {
        sortDescending = new CommitTotalDescending();
    }

    @Test
    public void shouldSortDescending() {
        List<ContributorStats> stats = asList(new ContributorStats("a", "b", 1),
                new ContributorStats("a", "c", 5));
        stats.sort(sortDescending);
        assertThat(stats.get(0).getLogin()).isEqualTo("c");
        assertThat(stats.get(1).getLogin()).isEqualTo("b");
    }

    @Test
    public void shouldSortDescendingWhenTotalCommitAreEquals() {
        List<ContributorStats> stats = asList(new ContributorStats("a", "b", 1),
                new ContributorStats("a", "c", 1));
        stats.sort(sortDescending);
        assertThat(stats.get(0).getLogin()).isEqualTo("b");
        assertThat(stats.get(1).getLogin()).isEqualTo("c");
    }
}