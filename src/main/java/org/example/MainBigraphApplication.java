package org.example;

import org.bigraphs.framework.converter.bigrapher.BigrapherTransformator;
import org.bigraphs.framework.converter.jlibbig.JLibBigBigraphDecoder;
import org.bigraphs.framework.core.BigraphComposite;
import org.bigraphs.framework.core.BigraphFileModelManagement;
import org.bigraphs.framework.core.ControlStatus;
import org.bigraphs.framework.core.datatypes.FiniteOrdinal;
import org.bigraphs.framework.core.datatypes.StringTypedName;
import org.bigraphs.framework.core.exceptions.IncompatibleSignatureException;
import org.bigraphs.framework.core.exceptions.InvalidConnectionException;
import org.bigraphs.framework.core.exceptions.InvalidReactionRuleException;
import org.bigraphs.framework.core.exceptions.operations.IncompatibleInterfaceException;
import org.bigraphs.framework.core.factory.BigraphFactory;
import org.bigraphs.framework.core.impl.elementary.Linkings;
import org.bigraphs.framework.core.impl.elementary.Placings;
import org.bigraphs.framework.core.impl.pure.PureBigraph;
import org.bigraphs.framework.core.impl.pure.PureBigraphBuilder;
import org.bigraphs.framework.core.impl.signature.DefaultDynamicSignature;
import org.bigraphs.framework.core.impl.signature.DynamicSignatureBuilder;
import org.bigraphs.framework.core.reactivesystem.BigraphMatch;
import org.bigraphs.framework.core.reactivesystem.ParametricReactionRule;
import org.bigraphs.framework.core.reactivesystem.ReactionRule;
import org.bigraphs.framework.simulation.matching.AbstractBigraphMatcher;
import org.bigraphs.framework.simulation.matching.MatchIterable;
import org.bigraphs.framework.simulation.matching.pure.PureBigraphParametricMatch;
import org.bigraphs.framework.simulation.matching.pure.PureReactiveSystem;

import java.io.IOException;
import java.util.Iterator;

import static org.bigraphs.framework.core.factory.BigraphFactory.*;

/**
 * This project provides a quick introduction on how to setup and use the Bigraph Framework.
 * <p>
 * This main class creates a signature and two bigraphs, and performs the following operations:
 * <ul>
 *     <li>The two bigraphs are composed</li>
 *     <li>A reactive system is created, a matching conducted and the agent rewritten</li>
 *     <li>A bigraph is converted to the BigraphER specification language format</li>
 * </ul>
 * <p>
 * Some intermediary results are printed to the command line.
 *
 * @author Dominik Grzelak
 */
public class MainBigraphApplication {

    public static void main(String[] args) throws InvalidConnectionException, InvalidReactionRuleException, IncompatibleInterfaceException, IOException {

//        new MainBigraphApplication().getting_started();

        DefaultDynamicSignature signature = pureSignatureBuilder()
                .addControl("A", 0)
                .newControl(StringTypedName.of("B"), FiniteOrdinal.ofInteger(1)).assign()
                .newControl().identifier(StringTypedName.of("C")).arity(FiniteOrdinal.ofInteger(2)).status(ControlStatus.ATOMIC).assign()
                .create();

        PureBigraph bigraph1 = pureBuilder(signature)
                .createRoot()
                .addChild("A").down().addChild("B")
                .createBigraph();

        PureBigraph bigraph2 = pureBuilder(signature)
                .createRoot()
                .addChild("C", "alice")
                .addSite()
                .createBigraph();

        System.out.println("\n-------------------------------");
        System.out.println("# Composing two bigraphs");
        System.out.println("-------------------------------");
        BigraphComposite<DefaultDynamicSignature> bigraphComposite = ops(bigraph2).compose(bigraph1);
        PureBigraph result = bigraphComposite.getOuterBigraph();
        BigraphFileModelManagement.Store.exportAsInstanceModel(result, System.out);

        System.out.println("\n-------------------------------");
        System.out.println("# Reactive System Creation, Matching and Rewriting");
        System.out.println("-------------------------------");
        PureReactiveSystem reactiveSystem = new PureReactiveSystem();
        reactiveSystem.setAgent(result);

        PureBigraph redex = pureBuilder(signature).createRoot().addChild("B").createBigraph();
        PureBigraph reactum = pureBuilder(signature).createRoot().addChild("A").createBigraph();
        ReactionRule<PureBigraph> rr = new ParametricReactionRule<>(redex, reactum);
        reactiveSystem.addReactionRule(rr);

        AbstractBigraphMatcher<PureBigraph> matcher = AbstractBigraphMatcher.create(PureBigraph.class);
        MatchIterable<BigraphMatch<PureBigraph>> match = matcher.match(reactiveSystem.getAgent(), reactiveSystem.getReactionRulesMap().get("r0"));
        Iterator<BigraphMatch<PureBigraph>> iterator = match.iterator();
        JLibBigBigraphDecoder decoder = new JLibBigBigraphDecoder();
        while (iterator.hasNext()) {
            System.out.println("- Found a match for given agent and RR");
            BigraphMatch<PureBigraph> next = iterator.next();
            System.out.println("\n-> Context:");
            BigraphFileModelManagement.Store.exportAsInstanceModel(decoder.decode(((PureBigraphParametricMatch) next).getJLibMatchResult().getContext(), signature), System.out);
            PureBigraph rewritten = reactiveSystem.buildParametricReaction(reactiveSystem.getAgent(), next, reactiveSystem.getReactionRulesMap().get("r0"));
            System.out.println("\n-> Rewritten Agent:");
            BigraphFileModelManagement.Store.exportAsInstanceModel(rewritten, System.out);
        }
        System.out.println("-------------------------------");

        System.out.println("\n-------------------------------");
        System.out.println("# Conversion to BigraphER");
        System.out.println("-------------------------------");
        BigrapherTransformator encoder = new BigrapherTransformator();
        String output = encoder.toString(reactiveSystem);
        System.out.println(output);
        System.out.println("-------------------------------");
    }

    public void getting_started() throws InvalidConnectionException, IncompatibleSignatureException, IncompatibleInterfaceException, IOException {
        DynamicSignatureBuilder signatureBuilder = pureSignatureBuilder();
        DefaultDynamicSignature signature = signatureBuilder.newControl().identifier("User").arity(1).status(ControlStatus.ATOMIC).assign().newControl(StringTypedName.of("Computer"), FiniteOrdinal.ofInteger(2)).assign().create();
        PureBigraphBuilder<DefaultDynamicSignature> builder = pureBuilder(signature);
        builder.createRoot().addChild("User", "login").addChild("Computer", "login");
        PureBigraph bigraph = builder.createRoot().addChild("User", "login").addChild("Computer", "login").createBigraph();

        Placings<DefaultDynamicSignature> placings = purePlacings(signature);
        Placings<DefaultDynamicSignature>.Merge merge = placings.merge(2);
        Linkings<DefaultDynamicSignature> linkings = pureLinkings(signature);
        Linkings<DefaultDynamicSignature>.Identity login = linkings.identity(StringTypedName.of("login"));
        BigraphComposite<DefaultDynamicSignature> composed = BigraphFactory.ops(merge).parallelProduct(login).compose(bigraph);
    }
}
