package org.lukosan.salix.thymeleaf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.spring4.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.spring4.view.ThymeleafView;
import org.thymeleaf.standard.expression.FragmentSelectionUtils;
import org.thymeleaf.standard.expression.ParsedFragmentSelection;
import org.thymeleaf.standard.expression.ProcessedFragmentSelection;

public class SalixView extends ThymeleafView {

	protected void renderFragment(final Set<String> markupSelectorsToRender, final Map<String, ?> model, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		final ServletContext servletContext = getServletContext();
		final String viewTemplateName = getTemplateName();
		final ITemplateEngine viewTemplateEngine = getTemplateEngine();

		if (viewTemplateName == null) {
			throw new IllegalArgumentException("Property 'templateName' is required");
		}
		if (getLocale() == null) {
			throw new IllegalArgumentException("Property 'locale' is required");
		}
		if (viewTemplateEngine == null) {
			throw new IllegalArgumentException("Property 'templateEngine' is required");
		}

		final Map<String, Object> mergedModel = new HashMap<String, Object>(30);
		final Map<String, Object> templateStaticVariables = getStaticVariables();
		if (templateStaticVariables != null) {
			mergedModel.putAll(templateStaticVariables);
		}
		if (model != null) {
			mergedModel.putAll(model);
		}

		final ApplicationContext applicationContext = getApplicationContext();

		final RequestContext requestContext = new RequestContext(request, response, getServletContext(), mergedModel);

		// For compatibility with ThymeleafView
		addRequestContextAsVariable(mergedModel, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
		// For compatibility with AbstractTemplateView
		addRequestContextAsVariable(mergedModel, AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);

		// Expose Thymeleaf's own evaluation context as a model variable
		//
		// Note Spring's EvaluationContexts are NOT THREAD-SAFE (in exchange for
		// SpelExpressions being thread-safe).
		// That's why we need to create a new EvaluationContext for each request
		// / template execution, even if it is
		// quite expensive to create because of requiring the initialization of
		// several ConcurrentHashMaps.
		final ConversionService conversionService = (ConversionService) request.getAttribute(ConversionService.class.getName()); // might
																																	// be
																																	// null!
		final ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext, conversionService);
		mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

		final WebExpressionContext context = new WebExpressionContext(viewTemplateEngine.getConfiguration(), request, response, servletContext,
				getLocale(), mergedModel);

		final String templateName;
		final Set<String> markupSelectors;
		if (!viewTemplateName.contains("::")) {
			// No fragment specified at the template name

			templateName = viewTemplateName;
			markupSelectors = null;

		} else {
			// Template name contains a fragment name, so we should parse it as
			// such

			final ParsedFragmentSelection parsedFragmentSelection = FragmentSelectionUtils.parseFragmentSelection(context, viewTemplateName);
			if (parsedFragmentSelection == null) {
				throw new IllegalArgumentException("Invalid template name specification: '" + viewTemplateName + "'");
			}

			final ProcessedFragmentSelection processedFragmentSelection = FragmentSelectionUtils.processFragmentSelection(context,
					parsedFragmentSelection);

			templateName = processedFragmentSelection.getTemplateName();
			markupSelectors = Collections.singleton(processedFragmentSelection.getFragmentSelector());
			final Map<String, Object> nameFragmentParameters = processedFragmentSelection.getFragmentParameters();

			if (nameFragmentParameters != null) {

				if (FragmentSelectionUtils.parameterNamesAreSynthetic(nameFragmentParameters.keySet())) {
					// We cannot allow synthetic parameters because there is no
					// way to specify them at the template
					// engine execution!
					throw new IllegalArgumentException("Parameters in a view specification must be named (non-synthetic): '" + viewTemplateName + "'");
				}

				context.setVariables(nameFragmentParameters);

			}

		}

		final String templateContentType = getContentType();
		final Locale templateLocale = getLocale();
		final String templateCharacterEncoding = getCharacterEncoding();

		final Set<String> processMarkupSelectors;
		if (markupSelectors != null && markupSelectors.size() > 0) {
			if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
				throw new IllegalArgumentException("A markup selector has been specified (" + Arrays.asList(markupSelectors) + ") for a view "
						+ "that was already being executed as a fragment (" + Arrays.asList(markupSelectorsToRender) + "). "
						+ "Only one fragment selection is allowed.");
			}
			processMarkupSelectors = markupSelectors;
		} else {
			if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
				processMarkupSelectors = markupSelectorsToRender;
			} else {
				processMarkupSelectors = null;
			}
		}

		response.setLocale(templateLocale);
		if (templateContentType != null) {
			response.setContentType(templateContentType);
		} else {
			response.setContentType(DEFAULT_CONTENT_TYPE);
		}
		if (templateCharacterEncoding != null) {
			response.setCharacterEncoding(templateCharacterEncoding);
		}

		Map<String,Object> templateResolutionAttributes = new HashMap<String,Object>();
		if(context.containsVariable(SalixContext.SCOPE)) {
			templateResolutionAttributes.put(SalixContext.SCOPE, context.getVariable(SalixContext.SCOPE));
		} else {
			templateResolutionAttributes.put(SalixContext.SCOPE, request.getServerName());
		}
			
		viewTemplateEngine.process(new TemplateSpec(templateName, processMarkupSelectors, null, templateResolutionAttributes), context, response.getWriter());
	}

}
