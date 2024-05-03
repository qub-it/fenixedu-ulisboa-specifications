/**
 * OMNIS.CLOUD SOFTWARE LICENSE
 *
 * Copyright (C) 2008-2021 Quorum Born IT <https://www.qub-it.com>
 * All rights reserved.
 *
 * 1. This software and all its associated and derivative rights belong
 * to Quorum Born IT (Quorum Born IT).
 *
 * 2. Copying, redistribution and use of this software, in source code
 * and/or binary forms are exclusive to Quorum Born IT and/or its partners.
 * The terms of the agreements between Quorum Born IT and its partners must
 * be defined in a written and valid partnership agreement, signed by both
 * entities.
 *
 * 3. Commercial use in source code and/or binary forms are exclusive to
 * Quorum Born IT, being licensed and not sold.
 *
 * 4. Quorum Born IT may grant a limited and controlled license to other
 * entities to use this software, following established partner agreements,
 * but commercial exploration and/or resale and/or sublicensing are not
 * allowed in any circunstances.
 *
 * 5. Quorum Born IT might authorize read-only access to this software in
 * the context of non-commercial use and/or within academic research and/or
 * educational contexts, provided that the following conditions are met:
 * 	a) All the individuals that will access the software must receive an
 * 	explicit authorization for it by Quorum Born IT, configured via
 * 	source-control system or similar mechanisms.
 * 	b) Quorum Born IT must be informed of the dates to end the access.
 * 	c) The software, being in source code and/or binary forms, is not
 * 	to be incorporated into proprietary software (unless belonging to
 * 	Quorum Born IT).
 *
 * 6. Modifications to this software by Quorum Born IT partners are permitted
 * provided that the following conditions are met:
 * 	a) Redistributions of source code must retain this license file.
 * 	b) Redistributions in binary form must reproduce this license file
 * 	in the documentation and/or other materials within the correspondent
 * 	distribution package.
 * 	c) Submitting back to Quorum Born IT all the modified versions of this
 * 	software source code.
 * 	d) Never to modify any binary forms of this software.
 * 	e) Unless it is Quorum Born IT incorporating received source code
 * 	modifications of this software in its base version, followed it with
 * 	the associated release, there is an explicit renounce to any Quorum
 * 	Born IT liability regarding the proper functioning of this software
 * 	and/or other software with which it interacts and/or integrates,
 * 	when modified by entities other than Quorum Born IT.
 *
 * 7. Activities involving the manipulation of this software not described in
 * this license, being in source code and/or binary forms, are outside its
 * scope and therefore are not allowed.
 *
 * 8. This software may not be error free, being licensed "as is" and
 * Quorum Born IT gives no express warranties, guarantees or conditions.
 * 	a) To the extent permitted under applicable laws, Quorum Born IT
 * 	excludes all implied warranties, including merchantability, fitness
 * 	for a particular purpose, and non-infringement.
 * 	b) Quorum Born IT shall not be liable for any lost profits, lost
 * 	revenues, lost opportunities, downtime, or any consequential damages
 * 	or costs, resulting from any claim or cause of action based on breach
 * 	of warranty, breach of contract, negligence, or any other legal theory,
 * 	that arises from the use of this software by others.
 *
 * 9. Everyone is permitted to copy and distribute verbatim copies of this
 * license document, but changes to it are not allowed.
 *
 *
 * Last updated: 13 July 2021
 */
package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;

@BennuSpringModule(basePackages = "com.qubit.qubEdu.module.candidacies", bundles = "CandidaciesExtensionsResources")
public class CandidaciesExtensionsConfiguration {

    public static final String BUNDLE = "resources/CandidaciesExtensionsResources";

}
