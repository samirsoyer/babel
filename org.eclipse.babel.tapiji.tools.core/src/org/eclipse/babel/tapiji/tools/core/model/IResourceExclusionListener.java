/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.model;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceExclusionEvent;

public interface IResourceExclusionListener {

    void exclusionChanged(ResourceExclusionEvent event);

}
