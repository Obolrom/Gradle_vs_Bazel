package com.romix.feature.feat572

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat572Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat572UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat572FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat572UserSummary
)

data class Feat572UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat572NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat572Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat572Config = Feat572Config()
) {

    fun loadSnapshot(userId: Long): Feat572NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat572NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat572UserSummary {
        return Feat572UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat572FeedItem> {
        val result = java.util.ArrayList<Feat572FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat572FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat572UiMapper {

    fun mapToUi(model: List<Feat572FeedItem>): Feat572UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat572UiModel(
            header = UiText("Feat572 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat572UiModel =
        Feat572UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat572UiModel =
        Feat572UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat572UiModel =
        Feat572UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat572Service(
    private val repository: Feat572Repository,
    private val uiMapper: Feat572UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat572UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat572UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat572UserItem1(val user: CoreUser, val label: String)
data class Feat572UserItem2(val user: CoreUser, val label: String)
data class Feat572UserItem3(val user: CoreUser, val label: String)
data class Feat572UserItem4(val user: CoreUser, val label: String)
data class Feat572UserItem5(val user: CoreUser, val label: String)
data class Feat572UserItem6(val user: CoreUser, val label: String)
data class Feat572UserItem7(val user: CoreUser, val label: String)
data class Feat572UserItem8(val user: CoreUser, val label: String)
data class Feat572UserItem9(val user: CoreUser, val label: String)
data class Feat572UserItem10(val user: CoreUser, val label: String)

data class Feat572StateBlock1(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock2(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock3(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock4(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock5(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock6(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock7(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock8(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock9(val state: Feat572UiModel, val checksum: Int)
data class Feat572StateBlock10(val state: Feat572UiModel, val checksum: Int)

fun buildFeat572UserItem(user: CoreUser, index: Int): Feat572UserItem1 {
    return Feat572UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat572StateBlock(model: Feat572UiModel): Feat572StateBlock1 {
    return Feat572StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat572UserSummary> {
    val list = java.util.ArrayList<Feat572UserSummary>(users.size)
    for (user in users) {
        list += Feat572UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat572UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat572UiModel {
    val summaries = (0 until count).map {
        Feat572UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat572UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat572UiModel> {
    val models = java.util.ArrayList<Feat572UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat572AnalyticsEvent1(val name: String, val value: String)
data class Feat572AnalyticsEvent2(val name: String, val value: String)
data class Feat572AnalyticsEvent3(val name: String, val value: String)
data class Feat572AnalyticsEvent4(val name: String, val value: String)
data class Feat572AnalyticsEvent5(val name: String, val value: String)
data class Feat572AnalyticsEvent6(val name: String, val value: String)
data class Feat572AnalyticsEvent7(val name: String, val value: String)
data class Feat572AnalyticsEvent8(val name: String, val value: String)
data class Feat572AnalyticsEvent9(val name: String, val value: String)
data class Feat572AnalyticsEvent10(val name: String, val value: String)

fun logFeat572Event1(event: Feat572AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat572Event2(event: Feat572AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat572Event3(event: Feat572AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat572Event4(event: Feat572AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat572Event5(event: Feat572AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat572Event6(event: Feat572AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat572Event7(event: Feat572AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat572Event8(event: Feat572AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat572Event9(event: Feat572AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat572Event10(event: Feat572AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat572Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat572Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat572(u: CoreUser): Feat572Projection1 =
    Feat572Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat572Projection1> {
    val list = java.util.ArrayList<Feat572Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat572(u)
    }
    return list
}
