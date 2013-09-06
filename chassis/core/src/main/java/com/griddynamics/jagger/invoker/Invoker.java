/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.invoker;

import java.io.Serializable;

/** Responsible for action invocation on specified endpoint and query
 * @author Mairbek Khadikov
 * @n
 * @par Details:
 * @details Create a request to some target with specified query. The result of invocation can be collected by metrics and validators. Note that Invoker is used in multi thread environment, so realize thread-safe implementation
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Invokers_Base_group */
public interface Invoker<Q,R,E> extends Serializable {


	/** Makes an invocation to target
     * @author Mairbek Khadikov
     * @n
     * @par Details:
     * @details If method throw some exception current invocation will be marked as failed
     * @n
     * @param query    - input data for the invocation
	 * @param endpoint - endpoint
     *
     * @return invocation result
     * @throws InvocationException when invocation failed */
      R invoke(Q query, E endpoint) throws InvocationException;

}

/* Below is doxygen documentation for Jagger customization */

/// @mainpage Custom components for Jagger
/// @n
/// @li @ref Main_Test_Suite_Flow_group
/// @li @ref Main_Test_Flow_group
/// @li @ref Main_Custom_Components_group

/// @defgroup Main_Test_Suite_Flow_group Test suite execution sequence
///
/// @dotfile jagger_flow.dot "Simplified test suite execution sequence"

/// @defgroup Main_Test_Flow_group Test execution sequence
///
/// @details Main components of test flow :
/// @li endpoint – where to apply test
/// @li query – what request to provide during test
/// @li distributor – how to combine endpoints and queries
/// @li invoker – how to transfer query to endpoint
/// @li collector – how to collect data
/// @n
/// @n
/// Click on diagram components to learn more about every component:
/// @li Interface description
/// @li Interface implementations in Jagger
/// @li How to customize component
/// @dotfile jagger_test_flow.dot "Simplified test execution sequence"
/// @n
/// To see full test suite execution sequence click here @ref Main_Test_Suite_Flow_group

/// @defgroup Main_Custom_Components_group Custom component
///
/// @li @ref Main_HowToCustomizeInvokers_group
/// @li @ref Main_HowToCustomizeProviders_group
/// @li @ref Main_HowToCustomizeDistributors_group
/// @li @ref Main_HowToCustomizeCollectors_group
/// @li @ref Main_HowToCustomizeDecisionMakers_group

/// @defgroup Main_Invokers_General_group General information about invokers
///
/// @li General information: @ref Main_Invokers_Base_group
/// @li Available implementations: @ref Main_Invokers_group
/// @li How to customize: @ref Main_HowToCustomizeInvokers_group

/* **************** How to customize invoker ************************* */
/// @defgroup Main_HowToCustomizeInvokers_group Custom invokers
///
/// @details
///
/// To add custom invoker you need to do:
///
/// 1. Create class which implements interface @ref Invoker<Q,R,E>
/// @dontinclude  PageVisitorInvoker.java
/// @skipline  public class PageVisitorInvoker
/// @n
///
/// 2. Create bean in XML file in the directory "suite/invokers/" with this class
/// @dontinclude  invokers.conf.xml
/// @skip  begin: following section is used for docu generation - invoker bean
/// @until end: following section is used for docu generation - invoker bean
/// @n
///
/// 3. Create component @xlink{invoker} with type @xlink{invoker-class} and set attribute @xlink{invoker-class,class} with full class name of invoker
/// @dontinclude  test.suite.scenario.config.xml
/// @skip  begin: following section is used for docu generation - invoker usage
/// @until end: following section is used for docu generation - invoker usage
/// @n
/// @b Note:
/// @li full examples of the code are available in maven archetype-examples
/// @li instead of ${package} write the name of your package
/// @li To view all invokers implementations click here @ref Main_Invokers_group


/* **************** Base components ************************* */
/// @defgroup Main_Invokers_Base_group Invoker
/// @details Invokers take abstract query and try to create invocation to abstract endpoint.
/// Every invoker returns some abstract result. Usually, query is used as http request and endpoint is used as url of target service
/// Invokers is used in @xlink{scenario-query-pool} element.
/// @n
/// To view all invokers implementations click here @ref Main_Invokers_group

/// @defgroup Main_Distributors_Base_group Distributor
/// @details Provides pairs of endpoints and queries for invoker.
/// @n
/// To view all distributors implementations click here @ref Main_Distributors_group

/// @defgroup Main_Collectors_Base_group Collector
/// @details There are two ways to collect some information from responses.
/// @li Create @xlink{metric}, which will calculate something and then aggregate this.
/// @li Create @xlink{validator}, which will validate results of invocations.
/// If validation fails, invocation will be mark as failed. So validator can affect success rate.
/// @n
/// @n
/// The results of collectors will be available in report and webUI.
/// @n
/// To view all collectors implementations click here @ref Main_Collectors_group


/* **************** Implementations ************************* */
/// @defgroup Main_Invokers_group Implementations of invokers

/// @defgroup Main_Providers_group Implementations of providers

/// @defgroup Main_Distributors_group Implementations of distributors

/// @defgroup Main_Collectors_group Implementations of collectors

/// @defgroup Main_DecisionMakers_group Implementations of decision makers

/// @defgroup Main_Terminators_group Implementations of termination strategies