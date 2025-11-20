package com.romix.feature.feat277

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat277Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat277UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat277FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat277UserSummary
)

data class Feat277UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat277NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat277Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat277Config = Feat277Config()
) {

    fun loadSnapshot(userId: Long): Feat277NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat277NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat277UserSummary {
        return Feat277UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat277FeedItem> {
        val result = java.util.ArrayList<Feat277FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat277FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat277UiMapper {

    fun mapToUi(model: List<Feat277FeedItem>): Feat277UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat277UiModel(
            header = UiText("Feat277 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat277UiModel =
        Feat277UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat277UiModel =
        Feat277UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat277UiModel =
        Feat277UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat277Service(
    private val repository: Feat277Repository,
    private val uiMapper: Feat277UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat277UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat277UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat277UserItem1(val user: CoreUser, val label: String)
data class Feat277UserItem2(val user: CoreUser, val label: String)
data class Feat277UserItem3(val user: CoreUser, val label: String)
data class Feat277UserItem4(val user: CoreUser, val label: String)
data class Feat277UserItem5(val user: CoreUser, val label: String)
data class Feat277UserItem6(val user: CoreUser, val label: String)
data class Feat277UserItem7(val user: CoreUser, val label: String)
data class Feat277UserItem8(val user: CoreUser, val label: String)
data class Feat277UserItem9(val user: CoreUser, val label: String)
data class Feat277UserItem10(val user: CoreUser, val label: String)

data class Feat277StateBlock1(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock2(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock3(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock4(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock5(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock6(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock7(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock8(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock9(val state: Feat277UiModel, val checksum: Int)
data class Feat277StateBlock10(val state: Feat277UiModel, val checksum: Int)

fun buildFeat277UserItem(user: CoreUser, index: Int): Feat277UserItem1 {
    return Feat277UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat277StateBlock(model: Feat277UiModel): Feat277StateBlock1 {
    return Feat277StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat277UserSummary> {
    val list = java.util.ArrayList<Feat277UserSummary>(users.size)
    for (user in users) {
        list += Feat277UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat277UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat277UiModel {
    val summaries = (0 until count).map {
        Feat277UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat277UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat277UiModel> {
    val models = java.util.ArrayList<Feat277UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat277AnalyticsEvent1(val name: String, val value: String)
data class Feat277AnalyticsEvent2(val name: String, val value: String)
data class Feat277AnalyticsEvent3(val name: String, val value: String)
data class Feat277AnalyticsEvent4(val name: String, val value: String)
data class Feat277AnalyticsEvent5(val name: String, val value: String)
data class Feat277AnalyticsEvent6(val name: String, val value: String)
data class Feat277AnalyticsEvent7(val name: String, val value: String)
data class Feat277AnalyticsEvent8(val name: String, val value: String)
data class Feat277AnalyticsEvent9(val name: String, val value: String)
data class Feat277AnalyticsEvent10(val name: String, val value: String)

fun logFeat277Event1(event: Feat277AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat277Event2(event: Feat277AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat277Event3(event: Feat277AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat277Event4(event: Feat277AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat277Event5(event: Feat277AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat277Event6(event: Feat277AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat277Event7(event: Feat277AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat277Event8(event: Feat277AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat277Event9(event: Feat277AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat277Event10(event: Feat277AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat277Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat277Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat277(u: CoreUser): Feat277Projection1 =
    Feat277Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat277Projection1> {
    val list = java.util.ArrayList<Feat277Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat277(u)
    }
    return list
}
