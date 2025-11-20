package com.romix.feature.feat191

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat191Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat191UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat191FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat191UserSummary
)

data class Feat191UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat191NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat191Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat191Config = Feat191Config()
) {

    fun loadSnapshot(userId: Long): Feat191NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat191NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat191UserSummary {
        return Feat191UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat191FeedItem> {
        val result = java.util.ArrayList<Feat191FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat191FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat191UiMapper {

    fun mapToUi(model: List<Feat191FeedItem>): Feat191UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat191UiModel(
            header = UiText("Feat191 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat191UiModel =
        Feat191UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat191UiModel =
        Feat191UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat191UiModel =
        Feat191UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat191Service(
    private val repository: Feat191Repository,
    private val uiMapper: Feat191UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat191UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat191UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat191UserItem1(val user: CoreUser, val label: String)
data class Feat191UserItem2(val user: CoreUser, val label: String)
data class Feat191UserItem3(val user: CoreUser, val label: String)
data class Feat191UserItem4(val user: CoreUser, val label: String)
data class Feat191UserItem5(val user: CoreUser, val label: String)
data class Feat191UserItem6(val user: CoreUser, val label: String)
data class Feat191UserItem7(val user: CoreUser, val label: String)
data class Feat191UserItem8(val user: CoreUser, val label: String)
data class Feat191UserItem9(val user: CoreUser, val label: String)
data class Feat191UserItem10(val user: CoreUser, val label: String)

data class Feat191StateBlock1(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock2(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock3(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock4(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock5(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock6(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock7(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock8(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock9(val state: Feat191UiModel, val checksum: Int)
data class Feat191StateBlock10(val state: Feat191UiModel, val checksum: Int)

fun buildFeat191UserItem(user: CoreUser, index: Int): Feat191UserItem1 {
    return Feat191UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat191StateBlock(model: Feat191UiModel): Feat191StateBlock1 {
    return Feat191StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat191UserSummary> {
    val list = java.util.ArrayList<Feat191UserSummary>(users.size)
    for (user in users) {
        list += Feat191UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat191UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat191UiModel {
    val summaries = (0 until count).map {
        Feat191UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat191UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat191UiModel> {
    val models = java.util.ArrayList<Feat191UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat191AnalyticsEvent1(val name: String, val value: String)
data class Feat191AnalyticsEvent2(val name: String, val value: String)
data class Feat191AnalyticsEvent3(val name: String, val value: String)
data class Feat191AnalyticsEvent4(val name: String, val value: String)
data class Feat191AnalyticsEvent5(val name: String, val value: String)
data class Feat191AnalyticsEvent6(val name: String, val value: String)
data class Feat191AnalyticsEvent7(val name: String, val value: String)
data class Feat191AnalyticsEvent8(val name: String, val value: String)
data class Feat191AnalyticsEvent9(val name: String, val value: String)
data class Feat191AnalyticsEvent10(val name: String, val value: String)

fun logFeat191Event1(event: Feat191AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat191Event2(event: Feat191AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat191Event3(event: Feat191AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat191Event4(event: Feat191AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat191Event5(event: Feat191AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat191Event6(event: Feat191AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat191Event7(event: Feat191AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat191Event8(event: Feat191AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat191Event9(event: Feat191AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat191Event10(event: Feat191AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat191Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat191Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat191(u: CoreUser): Feat191Projection1 =
    Feat191Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat191Projection1> {
    val list = java.util.ArrayList<Feat191Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat191(u)
    }
    return list
}
