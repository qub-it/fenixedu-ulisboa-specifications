package org.fenixedu.ulisboa.specifications.task;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;

public class InspectExistingServiceRequests extends CustomTask {

    @Override
    public void runTask() throws Exception {
        final AtomicInteger tuples = new AtomicInteger();
        Bennu.getInstance().getAcademicServiceRequestsSet().stream()
                .map(asr -> new Pair<Class<?>, ServiceRequestType>(asr.getClass(), asr.getServiceRequestType())).distinct()
                .forEach(pair -> pair.print(tuples.incrementAndGet(), Class::getSimpleName, ServiceRequestType::getName));
        taskLog("#Pairs: " + tuples.get());
    }

    class Pair<L, R> {
        private L l;
        private R r;

        public Pair(L l, R r) {
            this.l = l;
            this.r = r;
        }

        public L getLeft() {
            return l;
        }

        public R getRight() {
            return r;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair) {
                if (getLeft() == null && getRight() == null) {
                    return ((Pair<?, ?>) obj).getLeft() == null && ((Pair<?, ?>) obj).getRight() == null;
                } else if (getLeft() == null) {
                    return getRight().equals(((Pair<?, ?>) obj).getRight());
                } else if (getRight() == null) {
                    return getLeft().equals(((Pair<?, ?>) obj).getLeft());
                } else {
                    return getLeft().equals(((Pair<?, ?>) obj).getLeft()) && getRight().equals(((Pair<?, ?>) obj).getRight());
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (getLeft() == null && getRight() == null) {
                return 1;
            } else if (getLeft() == null) {
                return getRight().hashCode();
            } else if (getRight() == null) {
                return getLeft().hashCode();
            } else {
                return getLeft().hashCode() ^ getRight().hashCode();
            }
        }

        public void print(int order, Function<L, String> leftPrinter, Function<R, LocalizedString> rightPrinter) {
            String pretty =
                    String.format("%02d", order) + ". [" + (getLeft() != null ? leftPrinter.apply(getLeft()) : "<NULL>") + "]";
            for (int index = pretty.length(); index < 50; index++) {
                pretty += " ";
            }
            pretty +=
                    "[" + (getRight() != null ? rightPrinter.apply(getRight()).getContent(new Locale("pt", "PT")) : "<NULL>")
                            + "]";
            taskLog(pretty);
        }
    }
}
