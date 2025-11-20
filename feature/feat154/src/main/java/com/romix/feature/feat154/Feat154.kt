package com.romix.feature.feat154

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat154Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat154UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat154FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat154UserSummary
)

data class Feat154UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat154NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat154Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat154Config = Feat154Config()
) {

    fun loadSnapshot(userId: Long): Feat154NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat154NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat154UserSummary {
        return Feat154UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat154FeedItem> {
        val result = java.util.ArrayList<Feat154FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat154FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat154UiMapper {

    fun mapToUi(model: List<Feat154FeedItem>): Feat154UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat154UiModel(
            header = UiText("Feat154 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat154UiModel =
        Feat154UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat154UiModel =
        Feat154UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat154UiModel =
        Feat154UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat154Service(
    private val repository: Feat154Repository,
    private val uiMapper: Feat154UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat154UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat154UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat154UserItem1(val user: CoreUser, val label: String)
data class Feat154UserItem2(val user: CoreUser, val label: String)
data class Feat154UserItem3(val user: CoreUser, val label: String)
data class Feat154UserItem4(val user: CoreUser, val label: String)
data class Feat154UserItem5(val user: CoreUser, val label: String)
data class Feat154UserItem6(val user: CoreUser, val label: String)
data class Feat154UserItem7(val user: CoreUser, val label: String)
data class Feat154UserItem8(val user: CoreUser, val label: String)
data class Feat154UserItem9(val user: CoreUser, val label: String)
data class Feat154UserItem10(val user: CoreUser, val label: String)

data class Feat154StateBlock1(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock2(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock3(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock4(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock5(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock6(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock7(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock8(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock9(val state: Feat154UiModel, val checksum: Int)
data class Feat154StateBlock10(val state: Feat154UiModel, val checksum: Int)

fun buildFeat154UserItem(user: CoreUser, index: Int): Feat154UserItem1 {
    return Feat154UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat154StateBlock(model: Feat154UiModel): Feat154StateBlock1 {
    return Feat154StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat154UserSummary> {
    val list = java.util.ArrayList<Feat154UserSummary>(users.size)
    for (user in users) {
        list += Feat154UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat154UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat154UiModel {
    val summaries = (0 until count).map {
        Feat154UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat154UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat154UiModel> {
    val models = java.util.ArrayList<Feat154UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat154AnalyticsEvent1(val name: String, val value: String)
data class Feat154AnalyticsEvent2(val name: String, val value: String)
data class Feat154AnalyticsEvent3(val name: String, val value: String)
data class Feat154AnalyticsEvent4(val name: String, val value: String)
data class Feat154AnalyticsEvent5(val name: String, val value: String)
data class Feat154AnalyticsEvent6(val name: String, val value: String)
data class Feat154AnalyticsEvent7(val name: String, val value: String)
data class Feat154AnalyticsEvent8(val name: String, val value: String)
data class Feat154AnalyticsEvent9(val name: String, val value: String)
data class Feat154AnalyticsEvent10(val name: String, val value: String)

fun logFeat154Event1(event: Feat154AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat154Event2(event: Feat154AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat154Event3(event: Feat154AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat154Event4(event: Feat154AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat154Event5(event: Feat154AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat154Event6(event: Feat154AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat154Event7(event: Feat154AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat154Event8(event: Feat154AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat154Event9(event: Feat154AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat154Event10(event: Feat154AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat154Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat154Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat154(u: CoreUser): Feat154Projection1 =
    Feat154Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat154Projection1> {
    val list = java.util.ArrayList<Feat154Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat154(u)
    }
    return list
}
