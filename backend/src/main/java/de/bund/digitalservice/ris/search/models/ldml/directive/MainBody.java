package de.bund.digitalservice.ris.search.models.ldml.directive;

import de.bund.digitalservice.ris.search.models.ldml.MixedContentNode;

/**
 * Represents the main body section of a document in an XML-based model.
 *
 * <p>The `MainBody` class extends the `MixedContentNode` class and is used to encapsulate the
 * principal content or core structure of a document. As part of the XML schema, it may hold mixed
 * content, which can include both textual and non-textual nodes.
 *
 * <p>By inheriting from `MixedContentNode`, the `MainBody` class can manage and process a
 * collection of mixed content nodes, allowing for easy handling of complex hierarchical data.
 *
 * <p>This class is often utilized as a central component within other objects, like `Doc`, where it
 * represents the substantive content based on the schema definitions.
 */
public class MainBody extends MixedContentNode {}
