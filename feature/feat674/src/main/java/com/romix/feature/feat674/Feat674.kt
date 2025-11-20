package com.romix.feature.feat674

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat674Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat674UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat674FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat674UserSummary
)

data class Feat674UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat674NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat674Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat674Config = Feat674Config()
) {

    fun loadSnapshot(userId: Long): Feat674NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat674NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat674UserSummary {
        return Feat674UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat674FeedItem> {
        val result = java.util.ArrayList<Feat674FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat674FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat674UiMapper {

    fun mapToUi(model: List<Feat674FeedItem>): Feat674UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat674UiModel(
            header = UiText("Feat674 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat674UiModel =
        Feat674UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat674UiModel =
        Feat674UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat674UiModel =
        Feat674UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat674Service(
    private val repository: Feat674Repository,
    private val uiMapper: Feat674UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat674UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat674UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat674UserItem1(val user: CoreUser, val label: String)
data class Feat674UserItem2(val user: CoreUser, val label: String)
data class Feat674UserItem3(val user: CoreUser, val label: String)
data class Feat674UserItem4(val user: CoreUser, val label: String)
data class Feat674UserItem5(val user: CoreUser, val label: String)
data class Feat674UserItem6(val user: CoreUser, val label: String)
data class Feat674UserItem7(val user: CoreUser, val label: String)
data class Feat674UserItem8(val user: CoreUser, val label: String)
data class Feat674UserItem9(val user: CoreUser, val label: String)
data class Feat674UserItem10(val user: CoreUser, val label: String)

data class Feat674StateBlock1(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock2(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock3(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock4(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock5(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock6(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock7(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock8(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock9(val state: Feat674UiModel, val checksum: Int)
data class Feat674StateBlock10(val state: Feat674UiModel, val checksum: Int)

fun buildFeat674UserItem(user: CoreUser, index: Int): Feat674UserItem1 {
    return Feat674UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat674StateBlock(model: Feat674UiModel): Feat674StateBlock1 {
    return Feat674StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat674UserSummary> {
    val list = java.util.ArrayList<Feat674UserSummary>(users.size)
    for (user in users) {
        list += Feat674UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat674UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat674UiModel {
    val summaries = (0 until count).map {
        Feat674UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat674UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat674UiModel> {
    val models = java.util.ArrayList<Feat674UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat674AnalyticsEvent1(val name: String, val value: String)
data class Feat674AnalyticsEvent2(val name: String, val value: String)
data class Feat674AnalyticsEvent3(val name: String, val value: String)
data class Feat674AnalyticsEvent4(val name: String, val value: String)
data class Feat674AnalyticsEvent5(val name: String, val value: String)
data class Feat674AnalyticsEvent6(val name: String, val value: String)
data class Feat674AnalyticsEvent7(val name: String, val value: String)
data class Feat674AnalyticsEvent8(val name: String, val value: String)
data class Feat674AnalyticsEvent9(val name: String, val value: String)
data class Feat674AnalyticsEvent10(val name: String, val value: String)

fun logFeat674Event1(event: Feat674AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat674Event2(event: Feat674AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat674Event3(event: Feat674AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat674Event4(event: Feat674AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat674Event5(event: Feat674AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat674Event6(event: Feat674AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat674Event7(event: Feat674AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat674Event8(event: Feat674AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat674Event9(event: Feat674AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat674Event10(event: Feat674AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat674Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat674Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat674(u: CoreUser): Feat674Projection1 =
    Feat674Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat674Projection1> {
    val list = java.util.ArrayList<Feat674Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat674(u)
    }
    return list
}
