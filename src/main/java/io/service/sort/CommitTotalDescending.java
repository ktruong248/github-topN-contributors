package io.service.sort;

import io.service.ContributorStats;

import java.io.Serializable;
import java.util.Comparator;

public class CommitTotalDescending implements Comparator<ContributorStats>, Serializable {
    @Override
    public int compare(ContributorStats left, ContributorStats right) {

        if (left.getTotalCommits() == right.getTotalCommits()) {
            return 0;
        } else if (left.getTotalCommits() < right.getTotalCommits()) {
            return 1;
        }

        return -1;
    }
}
